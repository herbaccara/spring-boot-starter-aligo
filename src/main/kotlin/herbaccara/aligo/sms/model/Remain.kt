package herbaccara.aligo.sms.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Remain(
    @field:JsonProperty("SMS_CNT")
    val smsCnt: Int,
    @field:JsonProperty("LMS_CNT")
    val lmsCnt: Int,
    @field:JsonProperty("MMS_CNT")
    val mmsCnt: Int
)
