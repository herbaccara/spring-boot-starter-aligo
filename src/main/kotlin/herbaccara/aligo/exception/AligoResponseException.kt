package herbaccara.aligo.exception

class AligoResponseException(
    val resultCode: Int,
    override val message: String
) : RuntimeException(message)
