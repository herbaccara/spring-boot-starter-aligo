package herbaccara.boot.autoconfigure.aligo.talk

import org.springframework.boot.web.client.RestTemplateBuilder

interface AligoTalkRestTemplateBuilderCustomizer {

    fun customize(restTemplateBuilder: RestTemplateBuilder)
}
