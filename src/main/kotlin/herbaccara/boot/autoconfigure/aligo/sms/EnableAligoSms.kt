package herbaccara.boot.autoconfigure.aligo.sms

import org.springframework.context.annotation.Import
import java.lang.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(AligoSmsAutoConfiguration::class)
annotation class EnableAligoSms
