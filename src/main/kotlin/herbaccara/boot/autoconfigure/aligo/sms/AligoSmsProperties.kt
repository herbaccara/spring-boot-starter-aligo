package herbaccara.boot.autoconfigure.aligo.sms

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "aligo.sms")
@ConstructorBinding
data class AligoSmsProperties(
    val enabled: Boolean,
    val key: String,
    val userId: String,
    val failOnUnknownProperties: Boolean = false
)
