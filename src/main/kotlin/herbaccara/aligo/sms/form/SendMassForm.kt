package herbaccara.aligo.sms.form

import java.io.File
import java.time.LocalDateTime

data class SendMassForm(
    val sender: PhoneNumber,
    val receivers: List<Receiver> = emptyList(),
    val msgType: MsgType,
    val title: String? = null,
    val reservationDateTIme: LocalDateTime? = null,
    val images: List<File> = emptyList(),
    val testmode: Boolean = true
)
