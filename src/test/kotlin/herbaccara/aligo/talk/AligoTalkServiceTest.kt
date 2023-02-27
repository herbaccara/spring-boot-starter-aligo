package herbaccara.aligo.talk

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AligoTalkServiceTest {

    private fun String.plusId(): String {
        return this.firstOrNull()?.let { if (it != '@') "@$this" else this } ?: throw IllegalArgumentException()
    }

    @Test
    fun plusId() {
        val s = "testId"
        assertEquals(s.plusId(), "@$s")
    }
}
