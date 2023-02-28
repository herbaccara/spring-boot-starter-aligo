package herbaccara.aligo.sms.form

import java.io.File
import java.time.LocalDateTime

data class SendForm(
    val sender: PhoneNumber,
    val receivers: Map<PhoneNumber, CustomerName?> = emptyMap(),
    val msg: Message,
    val title: String? = null,
    val reservationDateTIme: LocalDateTime? = null,
    val images: List<File> = emptyList(),
    val msgType: MsgType? = null,
    val testmode: Boolean = true
) {
    companion object {
        fun of(
            sender: PhoneNumber,
            receiver: PhoneNumber,
            message: Message,
            customerName: CustomerName? = null,
            testmode: Boolean = true
        ): SendForm {
            return SendForm(
                sender,
                mapOf(receiver to customerName),
                message,
                testmode = testmode
            )
        }
    }
}
