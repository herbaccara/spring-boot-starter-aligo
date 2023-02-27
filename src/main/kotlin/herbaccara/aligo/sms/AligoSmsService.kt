package herbaccara.aligo.sms

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import herbaccara.aligo.sms.form.ListForm
import herbaccara.aligo.sms.form.SendForm
import herbaccara.aligo.sms.form.SendMassForm
import herbaccara.aligo.sms.form.SmsListForm
import herbaccara.boot.autoconfigure.aligo.sms.AligoSmsProperties
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import java.lang.RuntimeException
import java.time.format.DateTimeFormatter

class AligoSmsService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val properties: AligoSmsProperties
) {
    private val localDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val localTimeFormatter = DateTimeFormatter.ofPattern("HHmm")

    private inline fun <reified T> postForObject(
        uri: String,
        contentType: MediaType = MediaType.APPLICATION_FORM_URLENCODED,
        block: (map: MultiValueMap<String, Any>) -> Unit
    ): T {
        val headers = HttpHeaders().apply {
            this.contentType = contentType
        }

        val form = LinkedMultiValueMap<String, Any>()
            .apply {
                add("key", properties.key)
                add("user_id", properties.userId)
                block(this)
            }

        val httpEntity = HttpEntity(form, headers)

        val json = restTemplate.postForObject<JsonNode>(uri, httpEntity)

        val resultCode = json["result_code"].asInt()
        val message = json["message"].asText()
        if (resultCode < 0) {
            throw RuntimeException(message)
        }

        return objectMapper.readValue(json.toString())
    }

    fun send(form: SendForm): JsonNode {
        val uri = "/send"

        return postForObject(uri, MediaType.MULTIPART_FORM_DATA) { map ->
            map.add("sender", form.sender)
            map.add("receiver", form.receivers.joinToString(",") { it.receiver })
            map.add("msg", form.msg)
            if (form.msgType != null) {
                map.add("msg_type", form.msgType.name)
            }
            if (form.title != null) {
                map.add("title", form.title)
            }
            map.add("destination", form.receivers.joinToString(",") { it.receiver + "|" + (it.userName ?: "") })

            if (form.reservationDateTIme != null) {
                val (localDate, localTime) = form.reservationDateTIme.let {
                    it.toLocalDate() to it.toLocalTime()
                }
                map.add("rdate", localDateFormatter.format(localDate))
                map.add("rtime", localTimeFormatter.format(localTime))
            }
            form.images.forEachIndexed { i, f ->
                map.add("image${i + 1}", FileSystemResource(f))
            }
            if (form.testmode) {
                map.add("testmode_yn", "Y")
            }
        }
    }

    fun sendMass(form: SendMassForm): JsonNode {
        val uri = "/send_mass"

        return postForObject(uri, MediaType.MULTIPART_FORM_DATA) { map ->
            map.add("sender", form.sender)
            form.receivers.forEachIndexed { i, (receiver, message) ->
                map.add("rec_${i + 1}", receiver)
                map.add("msg_${i + 1}", message)
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
                map.add("rdate", localDateFormatter.format(localDate))
                map.add("rtime", localTimeFormatter.format(localTime))
            }
            form.images.forEachIndexed { i, f ->
                map.add("image${i + 1}", FileSystemResource(f))
            }
            if (form.testmode) {
                map.add("testmode_yn", "Y")
            }
        }
    }

    fun list(form: ListForm): JsonNode {
        val uri = "/list"

        return postForObject(uri) { map ->
            map.add("page", form.page)
            map.add("page_size", form.pageSize)
            if (form.startDate != null) {
                map.add("start_date", localDateFormatter.format(form.startDate))
            }
            if (form.limitDay != null) {
                map.add("limit_day", form.limitDay)
            }
        }
    }

    fun smsList(form: SmsListForm): JsonNode {
        val uri = "/sms_list"

        return postForObject(uri) { map ->
            map.add("mid", form.mid)
            map.add("page", form.page)
            map.add("page_size", form.pageSize)
        }
    }

    fun remain(): JsonNode {
        val uri = "/remain"

        return postForObject(uri) {
        }
    }

    fun cancel(mid: Int): JsonNode {
        val uri = "/cancel"

        return postForObject(uri) { map ->
            map.add("mid", mid)
        }
    }
}
