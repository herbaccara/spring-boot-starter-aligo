package herbaccara.aligo.talk

import com.fasterxml.jackson.databind.ObjectMapper
import herbaccara.aligo.talk.store.AligoTalkTokenStore
import herbaccara.boot.autoconfigure.aligo.talk.AligoTalkProperties
import org.springframework.web.client.RestTemplate

class AligoTalkService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val properties: AligoTalkProperties,
    private val tokenStore: AligoTalkTokenStore
)
