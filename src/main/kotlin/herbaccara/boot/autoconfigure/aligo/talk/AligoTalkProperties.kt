package herbaccara.boot.autoconfigure.aligo.talk

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConfigurationProperties(prefix = "aligo.talk")
@ConstructorBinding
data class AligoTalkProperties(
    val enabled: Boolean = true,
    val rootUri: String = "https://kakaoapi.aligo.in",
    val defaultTokenExpirationTime: Duration = Duration.ofDays(1)
)
