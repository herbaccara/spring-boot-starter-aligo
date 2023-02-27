package herbaccara.aligo.sms.exception

class AligoSmsResponseException(
    val resultCode: Int,
    override val message: String
) : RuntimeException(message)