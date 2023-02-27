package herbaccara.aligo.talk.model.token

import java.net.URLEncoder

data class AligoTalkToken(
    val token: String,
    /***
     * Access Token 만료 시간(초 단위)
     */
    val expiresIn: Long
) {
    fun urlEncodedToken(): String {
        return URLEncoder.encode(token, "UTF-8")
    }
}
