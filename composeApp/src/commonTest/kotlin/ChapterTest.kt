
import api.mangadex.model.request.ImageQuality
import api.mangadex.util.getChapterImageUrls
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class ChapterTest {
    private val chapterId = "85619903-1ce9-4a56-a464-3db7db9d3248"

    @Test
    fun getChapterUrls() = runBlocking {
        try {
            val res = api.getHomeUrl(chapterId)
            if (res != null) getChapterImageUrls(res, ImageQuality.DATA).forEach(::println) else
                throw Exception("no response")
        } catch (e: Exception) {
            println(e.message)
        }
    }
}