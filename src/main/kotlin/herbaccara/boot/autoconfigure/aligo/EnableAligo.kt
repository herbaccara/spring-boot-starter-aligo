package herbaccara.boot.autoconfigure.aligo

import herbaccara.boot.autoconfigure.aligo.sms.EnableAligoSms
import herbaccara.boot.autoconfigure.aligo.talk.EnableAligoTalk

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnableAligoSms
@EnableAligoTalk
annotation class EnableAligo
