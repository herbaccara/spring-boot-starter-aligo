package herbaccara.aligo.sms.form

data class SmsListForm(
    val mid: Int,
    val page: Int = 1,
    val pageSize: Int = 30 // 30 ~ 500
)
