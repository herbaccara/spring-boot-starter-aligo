package herbaccara.aligo.sms.form

import java.time.LocalDate

data class ListForm(
    val page: Int = 1,
    val pageSize: Int = 30, // 30 ~ 500
    val startDate: LocalDate? = null,
    val limitDay: Int? = null
)
