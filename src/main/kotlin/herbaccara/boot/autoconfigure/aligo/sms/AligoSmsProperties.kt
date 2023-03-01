package herbaccara.boot.autoconfigure.aligo.sms

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "aligo.sms")
@ConstructorBinding
data class AligoSmsProperties(
    val enabled: Boolean = true,
    val key: String,
    val userId: String,
    val rootUri: String = "https://apis.aligo.in"
)
