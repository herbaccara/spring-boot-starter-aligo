package herbaccara.boot.autoconfigure.aligo.sms

import org.springframework.boot.web.client.RestTemplateBuilder

interface AligoSmsRestTemplateBuilderCustomizer {

    fun customize(restTemplateBuilder: RestTemplateBuilder)
}
