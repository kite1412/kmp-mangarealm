package api.mangadex.util

object ApiConstant {
    const val BASE_URL = "https://api.mangadex.org"
    const val AUTH_ENDPOINT = "https://auth.mangadex.org/realms/mangadex/protocol/openid-connect/token"
    const val COVER_DISCOVERY_ENDPOINT = "https://uploads.mangadex.org/covers"

    const val MANGA_ENDPOINT = "$BASE_URL/manga"
    const val MANGA_STATUS = "$MANGA_ENDPOINT/status"
    const val TAGS_ENDPOINT = "$MANGA_ENDPOINT/tag"
    private const val MANGADEX_HOME = "$BASE_URL/at-home/server"
    const val USER_CUSTOM_LIST = "$BASE_URL/user/list"
    const val CUSTOM_LIST_ACTION = "$BASE_URL/list"

    fun mangaChapters(mangaId: String): String = "$MANGA_ENDPOINT/$mangaId/feed"

    fun chapterImagesUrl(chapterId: String): String = "$MANGADEX_HOME/$chapterId"

    fun updateMangaStatus(mangaId: String): String = "$MANGA_ENDPOINT/$mangaId/status"

    fun mangaToCustomList(mangaId: String, customListId: String): String =
        "$MANGA_ENDPOINT/$mangaId/list/$customListId"
}
