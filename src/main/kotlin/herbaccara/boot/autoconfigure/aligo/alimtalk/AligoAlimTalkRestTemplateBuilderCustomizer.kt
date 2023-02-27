package herbaccara.boot.autoconfigure.aligo.alimtalk

import org.springframework.boot.web.client.RestTemplateBuilder

interface AligoAlimTalkRestTemplateBuilderCustomizer {

    fun customize(restTemplateBuilder: RestTemplateBuilder)
}
