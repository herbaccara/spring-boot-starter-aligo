package herbaccara.aligo.alimtalk.store

import herbaccara.aligo.alimtalk.model.token.AligoAlimTalkToken
import java.util.concurrent.atomic.AtomicReference

/***
 * 분산 환경에서는 사용 금지
 */
class AligoAlimTalkInMemoryTokenStore : AligoAlimTalkTokenStore {

    private val reference: AtomicReference<AligoAlimTalkToken> = AtomicReference<AligoAlimTalkToken>()

    override fun save(token: AligoAlimTalkToken) {
        reference.set(token)
    }

    override fun load(): AligoAlimTalkToken? {
        return reference.get()
    }
}
