package herbaccara.aligo.sms

import herbaccara.aligo.sms.form.*
import herbaccara.boot.autoconfigure.aligo.sms.AligoSmsAutoConfiguration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.io.File
import java.time.LocalDateTime

// @Disabled
@SpringBootTest(
    classes = [
        AligoSmsAutoConfiguration::class
    ]
)
@TestPropertySource(locations = ["classpath:application.yml"])
class AligoSmsServiceTest {

    @Autowired
    lateinit var aligoSmsService: AligoSmsService

    private val sender: PhoneNumber = PhoneNumber("010789456")
    private val imageFile =
        AligoSmsServiceTest::class.java.getResource("/20180703190744-rollsafe-meme.jpeg")!!.let { File(it.path) }

    @Test
    fun sendMass() {
        val send = aligoSmsService.sendMass(
            SendMassForm(
                sender,
                listOf(
                    Receiver(PhoneNumber("01012345678"), Message("테스트 메시지 1")),
                    Receiver(PhoneNumber("01056781234"), Message("테스트 메시지 2"))
                ),
                MsgType.SMS, // 이미지 넣으면 자동으로 MMS 로 바뀐다.
                images = listOf(imageFile)
            )
        )
        println()
    }

    @Test
    fun send() {
        val send = aligoSmsService.send(
            SendForm(
                sender,
                mapOf(
                    PhoneNumber("01012345678") to CustomerName("홍길동")
                ),
                Message("테스트 메시지"),
                images = listOf(imageFile)
            )
        )
        println()
    }

    @Test
    fun cancel() {
        val form = SendForm.of(
            sender,
            PhoneNumber("01012345678"),
            Message("예약 문자 취소 테스트 할 메시지")
        ).copy(reservationDateTIme = LocalDateTime.now().plusDays(1))

        val send = aligoSmsService.send(form)

        val item = aligoSmsService.list(1).list.first()
        assertEquals("취소완료", item.reserve)

        if (item.reserve != "취소완료") {
            // NOTE : 테스트 메시지는 전송 즉시 예약-취소완료가 되기 때문에 오류 난다.
            val cancel = aligoSmsService.cancel(item.mid)
            println()
        }
    }

    @Test
    fun remain() {
        val remain = aligoSmsService.remain()
        println()
    }

    @Test
    fun list() {
        val list = aligoSmsService.list(1)
        println()
    }

    @Test
    fun smsList() {
        val map = aligoSmsService.list(1).list
            .map {
                aligoSmsService.smsList(it.mid)
            }
        println()
    }
}
