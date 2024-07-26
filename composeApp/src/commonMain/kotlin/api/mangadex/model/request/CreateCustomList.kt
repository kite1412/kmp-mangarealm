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

    companion object {
        fun fromString(value: String): Visibility = when(value) {
            "public" -> PUBLIC
            "private" -> PRIVATE
            else -> throw IllegalArgumentException("Invalid visibility value: $value")
        }
    }

    override fun toString(): String = when(this) {
        PUBLIC -> "public"
        PRIVATE -> "private"
    }
}