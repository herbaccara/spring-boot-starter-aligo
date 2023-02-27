package herbaccara.aligo

import java.time.format.DateTimeFormatter

internal object Constants {
    internal val localDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    internal val localTimeFormatter = DateTimeFormatter.ofPattern("HHmm")
}
