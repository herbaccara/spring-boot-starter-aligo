package herbaccara.aligo.talk

import herbaccara.boot.autoconfigure.aligo.talk.AligoTalkAutoConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.time.Duration

@SpringBootTest(
    classes = [
        AligoTalkAutoConfiguration::class
    ]
)
@TestPropertySource(locations = ["classpath:application.yml"])
//@Disabled
class AligoTalkServiceTest {

    @Autowired
    lateinit var aligoTalkService: AligoTalkService

    private fun String.plusId(): String {
        return this.firstOrNull()?.let { if (it != '@') "@$this" else this } ?: throw IllegalArgumentException()
    }

    @Test
    fun token() {
        val token = aligoTalkService.token(Duration.ofDays(30))
        println(token)
    }

    @Test
    fun category() {
        val category = aligoTalkService.category()
        println(category)
    }

    @Test
    fun plusId() {
        val s = "testId"
        assertEquals(s.plusId(), "@$s")
    }
}
