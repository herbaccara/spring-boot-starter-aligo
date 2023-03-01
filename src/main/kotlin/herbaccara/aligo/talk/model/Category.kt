package herbaccara.aligo.talk.model

data class Category(
    val firstBusinessType: List<BusinessType>,
    val secondBusinessType: List<BusinessType>,
    val thirdBusinessType: List<BusinessType>
) {
    data class BusinessType(
        val parentCode: String,
        val code: String,
        val name: String
    )
}
