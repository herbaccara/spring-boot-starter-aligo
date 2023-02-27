package herbaccara.aligo.sms.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Result(
    @field:JsonProperty("result_code")
    val resultCode: Int,
    val message: String
) {
    fun isError(): Boolean = resultCode < 0
}
