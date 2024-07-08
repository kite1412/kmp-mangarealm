import api.mangadex.util.generateArrayQueryParam
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class MangaTest {
    val mangaId = "dc332d04-d3b0-413c-a767-70f5e451b031"

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