package herbaccara.aligo.sms.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import herbaccara.aligo.sms.form.MsgType
import java.time.LocalDateTime

data class ListResult(
    val list: List<Item>,
    @field:JsonProperty("next_yn")
    val nextYn: String
) {
    fun hasNext(): Boolean = nextYn == "Y"

    data class Item(
        val mid: Int,
        val type: MsgType,

        val sender: String,

        @field:JsonProperty("sms_count")
        val smsCount: Int,

        @field:JsonProperty("reserve_state")
        val reserveState: String,

        val msg: String,

        @field:JsonProperty("fail_count")
        val failCount: Int,

        @field:JsonProperty("reg_date")
        @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val regDate: LocalDateTime,

        val reserve: String
    )
}
