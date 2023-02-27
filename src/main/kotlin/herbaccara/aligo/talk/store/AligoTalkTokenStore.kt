package herbaccara.aligo.talk.store

import herbaccara.aligo.talk.model.token.AligoTalkToken

interface AligoTalkTokenStore {

    fun save(token: AligoTalkToken)

    fun load(): AligoTalkToken?
}
