package herbaccara.boot.autoconfigure.aligo.talk

import org.springframework.context.annotation.Import
import java.lang.annotation.*

@Target
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(AligoTalkAutoConfiguration::class)
annotation class EnableAligoTalk
