package herbaccara.aligo.sms

import herbaccara.boot.autoconfigure.aligo.sms.AligoSmsAutoConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(
    classes = [
        AligoSmsAutoConfiguration::class
    ]
)
@TestPropertySource(locations = ["classpath:application.yml"])
class AligoSmsServiceTest {

    @Autowired
    lateinit var aligoSmsService: AligoSmsService

    @Test
    fun list() {
        val list = aligoSmsService.list(1)
        println()
    }
}
