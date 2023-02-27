package herbaccara.aligo.talk.model.token

data class AligoTalkToken(
    val token: String,
    /***
     * Access Token 만료 시간(초 단위)
     */
    val expiresIn: Long
)
