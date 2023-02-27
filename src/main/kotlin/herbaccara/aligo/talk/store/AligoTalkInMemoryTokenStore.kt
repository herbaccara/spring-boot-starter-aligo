package herbaccara.aligo.talk.store

import herbaccara.aligo.talk.model.token.AligoTalkToken
import java.util.concurrent.atomic.AtomicReference

/***
 * 분산 환경에서는 사용 금지
 */
class AligoTalkInMemoryTokenStore : AligoTalkTokenStore {

    private val reference: AtomicReference<AligoTalkToken> = AtomicReference<AligoTalkToken>()

    override fun save(token: AligoTalkToken) {
        reference.set(token)
    }

    override fun load(): AligoTalkToken? {
        return reference.get()
    }
}
