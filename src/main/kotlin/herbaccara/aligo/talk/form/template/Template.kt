package herbaccara.aligo.talk.form.template

import com.fasterxml.jackson.annotation.JsonInclude
import java.io.File

data class Template(
    val name: String,
    val content: String,
    val secure: Boolean = false,
    val type: Type? = null,
    val emType: EmType? = null,
    val extra: String? = null,
    val title: String? = null,
    val subTitle: String? = null,
    val image: File? = null,
    val buttons: List<Button> = emptyList()
) {
    enum class Type(val text: String) {
        BA("기본형"),
        EX("부가 정보형"),
        AD("광고 추가형"),
        MI("복합형")
    }

    enum class EmType(val text: String) {
        NONE("선택안함"),
        TEXT("강조표기형"),
        IMAGE("이미지형")
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Button(
        val name: String,
        val linkType: LinkType,
        val linkM: String? = null,
        val linkP: String? = null,
        val linkI: String? = null,
        val linkA: String? = null
    ) {
        enum class LinkType(val text: String) {
            AC("채널추가"),
            DS("배송조회"),
            WL("웹링크"),
            AL("앱링크"),
            BK("봇키워드"),
            MD("메시지전달")
        }
    }
}
