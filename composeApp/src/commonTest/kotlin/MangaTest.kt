
import api.mangadex.service.MangaDexImpl
import api.mangadex.service.TokenHandler
import api.mangadex.util.generateArrayQueryParam
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class MangaTest {
    private val api = MangaDexImpl(token = object : TokenHandler {
        override suspend fun invoke(): Boolean {
            return false
        }
    })

    @Test
    fun getManga() = runBlocking {
        try {
            val query = generateArrayQueryParam("includes[]", listOf(
                "manga",
                "cover_art",
            ))
            val res = api.getManga(query)
            println(res?.data?.size)
            res?.data?.forEach {
                it.relationships.forEach {r ->
                    r.attributes?.description?.let { d ->
                        println(d::class)
                    }
                }
            }
            println("<<<<<Success>>>>>")
        } catch (e: Throwable) {
            throw e
        }
    }
}