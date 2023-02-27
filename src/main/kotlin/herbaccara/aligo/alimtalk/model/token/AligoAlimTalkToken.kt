package herbaccara.aligo.alimtalk.model.token

import com.fasterxml.jackson.annotation.JsonProperty

data class AligoAlimTalkToken(
    val token: String,
    @field:JsonProperty("urlencode")
    val urlEncode: String
)
