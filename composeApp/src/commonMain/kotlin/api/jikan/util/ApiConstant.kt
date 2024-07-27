package api.jikan.util

object ApiConstant {
    private const val BASE_URL = "https://api.jikan.moe/v4"

    fun mangaCharacters(malId: Int): String = "$BASE_URL/manga/$malId/characters"
}