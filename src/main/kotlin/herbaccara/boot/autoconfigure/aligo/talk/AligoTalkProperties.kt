package herbaccara.boot.autoconfigure.aligo.talk

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "aligo.talk")
@ConstructorBinding
data class AligoTalkProperties(
    val enabled: Boolean = true,
    val apiKey: String,
    val userId: String,
    val rootUri: String = "https://kakaoapi.aligo.in",
    val failOnUnknownProperties: Boolean = false
)
