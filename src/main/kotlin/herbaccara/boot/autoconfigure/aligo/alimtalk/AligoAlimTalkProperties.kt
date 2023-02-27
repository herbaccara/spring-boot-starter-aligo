package herbaccara.boot.autoconfigure.aligo.alimtalk

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "aligo.alimtalk")
@ConstructorBinding
data class AligoAlimTalkProperties(
    val enabled: Boolean = true,
    val apiKey: String,
    val userId: String,
    val rootUri: String = "https://kakaoapi.aligo.in",
    val failOnUnknownProperties: Boolean = false
)
