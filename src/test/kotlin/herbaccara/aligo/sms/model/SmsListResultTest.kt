package herbaccara.aligo.sms.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SmsListResultTest {

    data class Pojo(
        @field:JsonProperty("send_date")
        @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val sendDate: LocalDateTime? = null,

        @field:JsonProperty("reserve_date")
        @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        val reserveDate: LocalDateTime? = null
    )

    @Test
    fun test() {
        val jacksonObjectMapper = jacksonObjectMapper().apply {
            findAndRegisterModules()
        }

        val json = """
            {
                "send_date" : "",
                "reserve_date" : "2023-02-28 19:55:50"
            }
        """.trimIndent()
        val readValue = jacksonObjectMapper.readValue<Pojo>(json)
        assertNull(readValue.sendDate)
        assertNotNull(readValue.reserveDate)
    }
}
