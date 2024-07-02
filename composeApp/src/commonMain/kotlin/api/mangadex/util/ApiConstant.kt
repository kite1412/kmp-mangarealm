package api.mangadex.util

object ApiConstant {
    const val BASE_URL = "https://api.mangadex.org"
    const val AUTH_ENDPOINT = "https://auth.mangadex.org/realms/mangadex/protocol/openid-connect/token"
    const val COVER_DISCOVERY_ENDPOINT = "https://uploads.mangadex.org/covers"

    const val MANGA_ENDPOINT = "$BASE_URL/manga"
    const val MANGA_STATUS = "$MANGA_ENDPOINT/status"
    const val TAGS_ENDPOINT = "$MANGA_ENDPOINT/tag"

    fun MANGA_CHAPTERS(mangaId: String): String {
        return "$MANGA_ENDPOINT/$mangaId/feed"
    }
}
