package herbaccara.aligo.talk.model.token

import com.fasterxml.jackson.annotation.JsonProperty

data class AligoTalkToken(
    val token: String,
    @field:JsonProperty("urlencode")
    val urlEncode: String
)
