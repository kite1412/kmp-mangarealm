
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class PagingTest {
    private val mock = ListResponse<MangaAttributes>(limit = 10, offset = 10, total = 200)

    @Test
    fun getNextPageManga() = runBlocking {
        try {
            val res = api.paging.manga(mock) ?: throw Exception("no response")
            println(res.data.size)
        } catch (e: Exception) {
            throw e
        }
    }
}