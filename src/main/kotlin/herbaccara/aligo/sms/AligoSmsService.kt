package herbaccara.aligo.sms

import herbaccara.boot.autoconfigure.aligo.sms.AligoSmsProperties
import org.springframework.web.client.RestTemplate

class AligoSmsService(
    private val restTemplate: RestTemplate,
    private val properties: AligoSmsProperties
)
