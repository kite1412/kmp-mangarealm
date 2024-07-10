import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import kotlinx.coroutines.runBlocking
import util.retry
import kotlin.test.Test

class RetryTest {
    val mockResponse = ListResponse<MangaAttributes>()

    @Test
    fun testRetry() = runBlocking {
        val res = retry<ListResponse<MangaAttributes>>(
            count = 3,
            predicate = { it.errors != null }
        ) {
            println("retry: $it")
            mockResponse
        }
        println(res)
    }
}