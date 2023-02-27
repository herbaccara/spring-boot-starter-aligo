package herbaccara.aligo.sms.form

import java.io.File
import java.time.LocalDateTime

data class SendForm(
    val sender: String,
    val receivers: List<Receiver> = emptyList(),
    val msg: String,
    val title: String? = null,
    val reservationDateTIme: LocalDateTime? = null,
    val images: List<File> = emptyList(),
    val msgType: MsgType? = null,
    val testmode: Boolean = true
) {
    data class Receiver(val receiver: String, val userName: String? = null)
}
