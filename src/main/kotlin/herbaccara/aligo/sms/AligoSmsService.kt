package herbaccara.aligo.sms

import com.fasterxml.jackson.databind.JsonNode
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
import java.time.format.DateTimeFormatter

class AligoSmsService(
    private val restTemplate: RestTemplate,
    private val properties: AligoSmsProperties
) {
    private val localDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val localTimeFormatter = DateTimeFormatter.ofPattern("HHmm")

    private fun httpEntity(
        contentType: MediaType = MediaType.APPLICATION_FORM_URLENCODED,
        block: (map: MultiValueMap<String, Any>) -> Unit
    ): HttpEntity<LinkedMultiValueMap<String, Any>> {
        val headers = HttpHeaders().apply {
            this.contentType = contentType
        }

        val form = LinkedMultiValueMap<String, Any>()
            .apply {
                add("key", properties.key)
                add("user_id", properties.userId)
                block(this)
            }

        return HttpEntity(form, headers)
    }

    fun send(form: SendForm): JsonNode {
        val uri = "/send"

        val httpEntity = httpEntity(MediaType.MULTIPART_FORM_DATA) { map ->
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

        return restTemplate.postForObject(uri, httpEntity)
    }

    fun sendMass(form: SendMassForm): JsonNode {
        val uri = "/send_mass"

        val httpEntity = httpEntity(MediaType.MULTIPART_FORM_DATA) { map ->
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

        return restTemplate.postForObject(uri, httpEntity)
    }

    fun list(form: ListForm): JsonNode {
        val uri = "/list"

        val httpEntity = httpEntity { map ->
            map.add("page", form.page)
            map.add("page_size", form.pageSize)
            if (form.startDate != null) {
                map.add("start_date", localDateFormatter.format(form.startDate))
            }
            if (form.limitDay != null) {
                map.add("limit_day", form.limitDay)
            }
        }

        return restTemplate.postForObject(uri, httpEntity)
    }

    fun smsList(form: SmsListForm): JsonNode {
        val uri = "/sms_list"

        val httpEntity = httpEntity { map ->
            map.add("mid", form.mid)
            map.add("page", form.page)
            map.add("page_size", form.pageSize)
        }

        return restTemplate.postForObject(uri, httpEntity)
    }

    fun remain() {
        val uri = "/remain"

        val httpEntity = httpEntity {}

        return restTemplate.postForObject(uri, httpEntity)
    }

    fun cancel(mid: Int) {
        val uri = "/cancel"

        val httpEntity = httpEntity { map ->
            map.add("mid", mid)
        }

        return restTemplate.postForObject(uri, httpEntity)
    }
}
