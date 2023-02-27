package herbaccara.aligo.alimtalk.store

import herbaccara.aligo.alimtalk.model.token.AligoAlimTalkToken

interface AligoAlimTalkTokenStore {

    fun save(token: AligoAlimTalkToken)

    fun load(): AligoAlimTalkToken?
}
