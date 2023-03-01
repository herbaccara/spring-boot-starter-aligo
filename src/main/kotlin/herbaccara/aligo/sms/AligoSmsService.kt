package herbaccara.aligo.sms

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import herbaccara.aligo.Constants
import herbaccara.aligo.exception.AligoResponseException
import herbaccara.aligo.sms.form.ListForm
import herbaccara.aligo.sms.form.SendForm
import herbaccara.aligo.sms.form.SendMassForm
import herbaccara.aligo.sms.form.SmsListForm
import herbaccara.aligo.sms.model.ListResult
import herbaccara.aligo.sms.model.Remain
import herbaccara.aligo.sms.model.SendResult
import herbaccara.aligo.sms.model.SmsListResult
import herbaccara.boot.autoconfigure.aligo.AligoProperties
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import java.time.LocalDate

class AligoSmsService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val properties: AligoProperties
) {
    private inline fun <reified T> postForObject(
        uri: String,
        contentType: MediaType = MediaType.APPLICATION_FORM_URLENCODED,
        block: (map: MultiValueMap<String, Any>) -> Unit = { _ -> }
    ): T {
        val headers = HttpHeaders().apply {
            this.contentType = contentType
        }

        val form = LinkedMultiValueMap<String, Any>()
            .apply {
                add("key", properties.apiKey)
                add("user_id", properties.userId)
                block(this)
            }

        val httpEntity = HttpEntity(form, headers)

        // NOTE : 리턴값은 json 형식이긴한데.. content-type 이 text/html; charset=UTF-8 이다 -_-...
        val jsonText = restTemplate.postForObject<String>(uri, httpEntity)
        val json = objectMapper.readValue<JsonNode>(jsonText)

        val resultCode = json["result_code"].asInt()
        val message = json["message"].asText()
        if (resultCode < 0) {
            throw AligoResponseException(resultCode, "($resultCode) $message")
        }

        (json as ObjectNode).apply {
            remove("result_code")
            remove("message")
        }

        return objectMapper.readValue(json.toString())
    }

    fun send(form: SendForm): SendResult {
        val uri = "/send/"

        return postForObject(uri, MediaType.MULTIPART_FORM_DATA) { map ->
            map.add("sender", form.sender.value)
            map.add("receiver", form.receivers.keys.joinToString(",") { phoneNumber -> phoneNumber.value })
            map.add("msg", form.msg)
            if (form.msgType != null) {
                map.add("msg_type", form.msgType.name)
            }
            if (form.title != null) {
                map.add("title", form.title)
            }
            map.add(
                "destination",
                form.receivers.map { (phoneNumber, customerName) ->
                    "${phoneNumber.value}|${customerName?.value ?: ""}"
                }.joinToString(",")
            )
            if (form.reservationDateTIme != null) {
                val (localDate, localTime) = form.reservationDateTIme.let {
                    it.toLocalDate() to it.toLocalTime()
                }
                map.add("rdate", Constants.localDateFormatter.format(localDate))
                map.add("rtime", Constants.localTimeFormatter.format(localTime))
            }
            form.images.forEachIndexed { i, f ->
                map.add("image${i + 1}", FileSystemResource(f))
            }
            if (form.testmode) {
                map.add("testmode_yn", "Y")
            }
        }
    }

    fun sendMass(form: SendMassForm): SendResult {
        val uri = "/send_mass/"

        return postForObject(uri, MediaType.MULTIPART_FORM_DATA) { map ->
            map.add("sender", form.sender.value)
            form.receivers.forEachIndexed { i, (receiver, message) ->
                map.add("rec_${i + 1}", receiver.value)
                map.add("msg_${i + 1}", message.value)
            }
            map.add("cnt", form.receivers.size)
            if (form.title != null) {
                map.add("title", form.title)
            }
            map.add("msg_type", form.msgType.name)

            if (form.reservationDateTIme != null) {
                val (localDate, localTime) = form.reservationDateTIme.let {
                    it.toLocalDate() to it.toLocalTime()
                }
                map.add("rdate", Constants.localDateFormatter.format(localDate))
                map.add("rtime", Constants.localTimeFormatter.format(localTime))
            }
            form.images.forEachIndexed { i, f ->
                map.add("image${i + 1}", FileSystemResource(f))
            }
            if (form.testmode) {
                map.add("testmode_yn", "Y")
            }
        }
    }

    @JvmOverloads
    fun list(page: Int = Constants.defaultPage, pageSize: Int = Constants.defaultPageSize, startDate: LocalDate? = null, limitDay: Int? = null): ListResult {
        return list(ListForm(page, pageSize, startDate, limitDay))
    }

    fun list(form: ListForm): ListResult {
        val uri = "/list/"

        return postForObject(uri) { map ->
            map.add("page", form.page)
            map.add("page_size", form.pageSize)
            if (form.startDate != null) {
                map.add("start_date", Constants.localDateFormatter.format(form.startDate))
            }
            if (form.limitDay != null) {
                map.add("limit_day", form.limitDay)
            }
        }
    }

    @JvmOverloads
    fun smsList(mid: Int, page: Int = Constants.defaultPage, pageSize: Int = Constants.defaultPageSize): SmsListResult {
        return smsList(SmsListForm(mid, page, pageSize))
    }

    fun smsList(form: SmsListForm): SmsListResult {
        val uri = "/sms_list/"

        return postForObject(uri) { map ->
            map.add("mid", form.mid)
            map.add("page", form.page)
            map.add("page_size", form.pageSize)
        }
    }

    fun remain(): Remain {
        val uri = "/remain/"

        return postForObject(uri)
    }

    fun cancel(mid: Int): JsonNode {
        val uri = "/cancel/"

        return postForObject(uri) { map ->
            map.add("mid", mid)
        }
    }
}
