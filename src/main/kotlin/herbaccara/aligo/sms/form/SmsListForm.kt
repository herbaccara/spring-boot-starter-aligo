package herbaccara.aligo.sms.form

import herbaccara.aligo.Constants

data class SmsListForm(
    val mid: Int,
    val page: Int = Constants.defaultPage,
    val pageSize: Int = Constants.defaultPageSize
)
