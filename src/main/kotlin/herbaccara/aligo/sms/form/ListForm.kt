package herbaccara.aligo.sms.form

import herbaccara.aligo.Constants
import java.time.LocalDate

data class ListForm(
    val page: Int = Constants.defaultPage,
    val pageSize: Int = Constants.defaultPageSize,
    val startDate: LocalDate? = null,
    val limitDay: Int? = null
)
