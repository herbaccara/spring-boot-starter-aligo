package herbaccara.boot.autoconfigure.aligo

import herbaccara.boot.autoconfigure.aligo.sms.AligoSmsProperties
import herbaccara.boot.autoconfigure.aligo.talk.AligoTalkProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "aligo")
@ConstructorBinding
data class AligoProperties(
    val apiKey: String,
    val userId: String,
    val sms: AligoSmsProperties = AligoSmsProperties(),
    val talk: AligoTalkProperties = AligoTalkProperties()
)
