package api.mangadex.model.request

data class CreateCustomList(
    val name: String,
    val visibility: Visibility,
    val manga: List<String> = listOf(),
    val version: Int? = null
)

enum class Visibility {
    PUBLIC,
    PRIVATE;

    override fun toString(): String = when(this) {
        PUBLIC -> "public"
        PRIVATE -> "private"
    }
}
