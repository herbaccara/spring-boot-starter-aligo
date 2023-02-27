package herbaccara.aligo.talk.model

@Deprecated("성공이면 필드 정보가 필요없고, 실패면 throw AligoResponseException(resultCode, message) 하기 때문에 사용할 이유가 없다.")
data class Result(
    val code: Int,
    val message: String
) {
    fun isError(): Boolean = code < 0
}
