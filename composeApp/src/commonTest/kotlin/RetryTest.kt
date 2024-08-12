import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import kotlinx.coroutines.runBlocking
import util.retry
import kotlin.test.Test

class RetryTest {
    private val mockResponse = ListResponse<MangaAttributes>()

    @Test
    fun retrySuccess() = runBlocking {
        val res = retry<ListResponse<MangaAttributes>>(
            maxAttempts = 3,
            predicate = { it.errors == null }
        ) {
            println("retry: $it")
            if (it == 2) {
                mockResponse.copy(errors = listOf())
            } else mockResponse
        }
        println(res)
    }

    @Test
    fun retryFail() = runBlocking {
        val retry = retry(
            maxAttempts = 3,
            predicate = { true }
        ) {
            println("retry: $it")
            true
        }
        println(retry)
    }
}