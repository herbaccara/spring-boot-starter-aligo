package herbaccara.aligo.talk.model

import com.fasterxml.jackson.annotation.JsonProperty

data class HeartInfo(
    @field:JsonProperty("SMS_CNT")
    val smsCnt: Int,
    @field:JsonProperty("LMS_CNT")
    val lmsCnt: Int,
    @field:JsonProperty("MMS_CNT")
    val mmsCnt: Int,
    @field:JsonProperty("ALT_CNT")
    val altCnt: Int,
    @field:JsonProperty("FTS_CNT")
    val ftsCnt: Int,
    @field:JsonProperty("FTM_CNT")
    val ftmCnt: Int
)
