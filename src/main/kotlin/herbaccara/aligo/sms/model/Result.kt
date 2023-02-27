package herbaccara.aligo.sms.model

import com.fasterxml.jackson.annotation.JsonProperty

@Deprecated("성공이면 필드 정보가 필요없고, 실패면 throw AligoSmsResponseException(resultCode, message) 하기 때문에 사용할 이유가 없다.")
data class Result(
    @field:JsonProperty("result_code")
    val resultCode: Int,
    val message: String
) {
    fun isError(): Boolean = resultCode < 0
}
