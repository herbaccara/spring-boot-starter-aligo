package herbaccara.boot.autoconfigure.aligo.alimtalk

import org.springframework.context.annotation.Import
import java.lang.annotation.*

@Target
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(AligoAlimTalkAutoConfiguration::class)
annotation class EnableAligoAlimTalk
