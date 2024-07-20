package api.mangadex.model.request

data class CreateCustomList(
    val name: String,
    val visibility: Visibility,
    val manga: List<String> = listOf(),
    val version: Int? = null
)

fun createPrivateCustomList(
    name: String,
    manga: List<String> = listOf(),
    version: Int? = null
): CreateCustomList = CreateCustomList(
    name = name,
    visibility = Visibility.PRIVATE,
    manga = manga,
    version = version
)

fun createPublicCustomList(
    name: String,
    manga: List<String> = listOf(),
    version: Int? = null
): CreateCustomList = CreateCustomList(
    name = name,
    visibility = Visibility.PUBLIC,
    manga = manga,
    version = version
)

enum class Visibility {
    PUBLIC,
    PRIVATE;

    override fun toString(): String = when(this) {
        PUBLIC -> "public"
        PRIVATE -> "private"
    }
}
