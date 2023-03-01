package herbaccara.aligo.talk

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import herbaccara.aligo.Constants
import herbaccara.aligo.exception.AligoResponseException
import herbaccara.aligo.talk.form.template.Template
import herbaccara.aligo.talk.model.Category
import herbaccara.aligo.talk.model.HeartInfo
import herbaccara.aligo.talk.model.token.AligoTalkToken
import herbaccara.aligo.talk.store.AligoTalkTokenStore
import herbaccara.boot.autoconfigure.aligo.AligoProperties
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import java.time.Duration
import java.time.LocalDate

class AligoTalkService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val properties: AligoProperties,
    private val tokenStore: AligoTalkTokenStore
) {
    private inline fun <reified T> postForObject(
        uri: String,
        withToken: Boolean = true,
        contentType: MediaType = MediaType.APPLICATION_FORM_URLENCODED,
        block: (map: MultiValueMap<String, Any>) -> Unit = { _ -> }
    ): T {
        val headers = HttpHeaders().apply {
            this.contentType = contentType
        }

        val form = LinkedMultiValueMap<String, Any>()
            .apply {
                add("apikey", properties.apiKey)
                add("userid", properties.userId)
                if (withToken) {
                    add("token", tokenStore.load()?.token)
                }
                block(this)
            }

        val httpEntity = HttpEntity(form, headers)

        // NOTE : 리턴값은 json 형식이긴한데.. content-type 이 text/html; charset=UTF-8 이다 -_-...
        val jsonText = restTemplate.postForObject<String>(uri, httpEntity)
        val json = objectMapper.readValue<JsonNode>(jsonText)

        val code = json["code"].asInt()
        val message = json["message"].asText()
        if (code < 0) {
            throw AligoResponseException(code, "($code) $message")
        }

        (json as ObjectNode).apply {
            remove("code")
            remove("message")
        }

        return objectMapper.readValue(json.toString())
    }

    @JvmOverloads
    fun token(duration: Duration = properties.talk.defaultTokenExpirationTime): AligoTalkToken {
        val seconds = duration.seconds
        val uri = "/akv10/token/create/$seconds/s/"

        val json = postForObject<JsonNode>(uri, false)
        return AligoTalkToken(json["token"].asText(), seconds).also { token ->
            tokenStore.save(token)
        }
    }

    private fun <T> recover(block: () -> T): T {
        return runCatching { block() }
            .recoverCatching { exception ->
                // (-107) 사용할수 없는 토큰 입니다. - 전송중 소실된 토큰 문자열
                // (-10) 토큰(=token) 파라메더가 전달되지 않았습니다.
                val recoverable = listOf(-10, -107)
                if (exception is AligoResponseException && recoverable.contains(exception.resultCode)) {
                    token()
                    return block()
                }
                throw exception
            }
            .getOrThrow()
    }

    fun category(): Category {
        return recover {
            val uri = "/akv10/category/"

            val json = postForObject<JsonNode>(uri)
            objectMapper.readValue(json["data"].toString())
        }
    }

    private fun String.plusId(): String {
        return this.firstOrNull()?.let { if (it != '@') "@$this" else this } ?: throw IllegalArgumentException()
    }

    fun profileAuth(plusId: String, phoneNumber: String): JsonNode {
        return recover {
            val uri = "/akv10/profile/auth/"

            postForObject(uri) { map ->
                map.add("plusid", plusId.plusId())
                map.add("phonenumber", phoneNumber)
            }
        }
    }

    fun profileAdd(plusId: String, authNum: String, phoneNumber: String, categoryCode: String): JsonNode {
        return recover {
            val uri = "/akv10/profile/add/"

            postForObject(uri) { map ->
                map.add("plusid", plusId.plusId())
                map.add("authnum", authNum)
                map.add("phonenumber", phoneNumber)
                map.add("categorycode", categoryCode)
            }
        }
    }

    @JvmOverloads
    fun profileList(plusId: String? = null, senderKey: String? = null): JsonNode {
        return recover {
            val uri = "/akv10/profile/list/"

            postForObject(uri) { map ->
                if (plusId != null) {
                    map.add("plusid", plusId.plusId())
                }
                if (senderKey != null) {
                    map.add("senderkey", senderKey)
                }
            }
        }
    }

    @JvmOverloads
    fun templateList(senderKey: String, templateCode: String? = null): JsonNode {
        return recover {
            val uri = "/akv10/template/list/"

            postForObject(uri) { map ->
                map.add("senderkey", senderKey)
                if (templateCode != null) {
                    map.add("tpl_code", templateCode)
                }
            }
        }
    }

    private fun toMultiValueMap(template: Template): MultiValueMap<String, Any> {
        return LinkedMultiValueMap<String, Any>().apply {
            add("tpl_name", template.name)
            add("tpl_content", template.content)
            add("tpl_secure", if (template.secure) "Y" else "N")
            if (template.type != null) {
                add("tpl_type", template.type.name)
            }
            if (template.emType != null) {
                add("tpl_emtype", template.emType.name)
            }
            if (template.extra != null) {
                add("tpl_extra", template.extra)
            }
            if (template.title != null) {
                add("tpl_title", template.title)
            }
            if (template.subTitle != null) {
                add("tpl_stitle", template.subTitle)
            }
            if (template.buttons.isNotEmpty()) {
                add("tpl_button", objectMapper.writeValueAsString(mapOf("button" to template.buttons)))
            }
            if (template.image != null) {
                add("image", FileSystemResource(template.image))
            }
        }
    }

    fun templateAdd(senderKey: String, template: Template): JsonNode {
        return recover {
            val uri = "/akv10/template/add/"

            postForObject(uri, contentType = MediaType.MULTIPART_FORM_DATA) { map ->
                map.add("senderkey", senderKey)
                map.addAll(toMultiValueMap(template))
            }
        }
    }

    fun templateModify(senderKey: String, templateCode: String, template: Template): JsonNode {
        return recover {
            val uri = "/akv10/template/modify/"

            postForObject(uri, contentType = MediaType.MULTIPART_FORM_DATA) { map ->
                map.add("senderkey", senderKey)
                map.add("tpl_code", templateCode)
                map.addAll(toMultiValueMap(template))
            }
        }
    }

    fun templateDel(senderKey: String, templateCode: String): JsonNode {
        return recover {
            val uri = "/akv10/template/del/"

            postForObject(uri) { map ->
                map.add("senderkey", senderKey)
                map.add("tpl_code", templateCode)
            }
        }
    }

    fun templateRequest(senderKey: String, templateCode: String): JsonNode {
        return recover {
            val uri = "/akv10/template/request/"

            postForObject(uri) { map ->
                map.add("senderkey", senderKey)
                map.add("tpl_code", templateCode)
            }
        }
    }

    // FIXME: /akv10/alimtalk/send/

    // FIXME: /akv10/friend/send/

    @JvmOverloads
    fun historyRequest(
        page: Int = Constants.defaultPage,
        limit: Int = Constants.defaultLimit,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): JsonNode {
        return recover {
            val uri = "/akv10/history/list/"

            postForObject(uri) { map ->
                map.add("page", page)
                map.add("limit", limit)
                if (startDate != null) {
                    map.add("startdate", Constants.localDateFormatter.format(startDate))
                }
                if (endDate != null) {
                    map.add("enddate", Constants.localDateFormatter.format(endDate))
                }
            }
        }
    }

    @JvmOverloads
    fun historyDetail(
        mid: String,
        page: Int = Constants.defaultPage,
        limit: Int = Constants.defaultLimit
    ): JsonNode {
        return recover {
            val uri = "/akv10/history/detail/"

            postForObject(uri) { map ->
                map.add("mid", mid)
                map.add("page", page)
                map.add("limit", limit)
            }
        }
    }

    fun heartInfo(): HeartInfo {
        return recover {
            val uri = "/akv10/heartinfo/"

            val json = postForObject<JsonNode>(uri)
            objectMapper.readValue(json["list"].toString())
        }
    }

    fun cancel(mid: String): JsonNode {
        return recover {
            val uri = "/akv10/cancel/"

            postForObject(uri) { map ->
                map.add("mid", mid)
            }
        }
    }
}
