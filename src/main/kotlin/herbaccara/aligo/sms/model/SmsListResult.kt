package herbaccara.aligo.sms.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import herbaccara.aligo.sms.form.MsgType
import java.time.LocalDateTime

data class SmsListResult(
    val list: List<Item>,
    @field:JsonProperty("next_yn")
    val nextYn: String
) {
    data class Item(
        val mdid: Int,
        val type: MsgType,
        val sender: String,
        val receiver: String,
        @field:JsonProperty("sms_state")
        val smsState: String,
        @field:JsonProperty("reg_date")
        @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val regDate: LocalDateTime,
        @field:JsonProperty("send_date")
        val sendDate: String? = null,
        @field:JsonProperty("reserve_date")
        val reserveDate: String? = null
    )
}
