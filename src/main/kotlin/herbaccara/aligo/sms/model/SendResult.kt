package herbaccara.aligo.sms.model

import com.fasterxml.jackson.annotation.JsonProperty
import herbaccara.aligo.sms.form.MsgType

data class SendResult(
    @field:JsonProperty("msg_id")
    val msgId: Int,
    @field:JsonProperty("success_cnt")
    val successCnt: Int,
    @field:JsonProperty("error_cnt")
    val errorCnt: Int,
    @field:JsonProperty("msg_type")
    val msgType: MsgType
)
