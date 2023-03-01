package herbaccara.aligo

import java.time.format.DateTimeFormatter

internal object Constants {
    internal val localDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    internal val localTimeFormatter = DateTimeFormatter.ofPattern("HHmm")

    internal const val defaultPage: Int = 1
    internal const val defaultPageSize: Int = 30
    internal const val defaultLimit: Int = 50
}
