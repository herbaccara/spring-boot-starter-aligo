package herbaccara.aligo.alimtalk

import com.fasterxml.jackson.databind.ObjectMapper
import herbaccara.aligo.alimtalk.store.AligoAlimTalkTokenStore
import herbaccara.boot.autoconfigure.aligo.alimtalk.AligoAlimTalkProperties
import org.springframework.web.client.RestTemplate

class AligoAlimTalkService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val properties: AligoAlimTalkProperties,
    private val tokenStore: AligoAlimTalkTokenStore
)
