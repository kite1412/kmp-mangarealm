import api.mangadex.util.getTagList
import kotlin.test.Test

class UtilFunsTest {
    @Test
    fun testGetTagsList() {
        val tags = "Romance, Drama, Psychological, Shounen, Seinen"
        val l = getTagList(tags).run {
            println(size)
            forEach {
                println(it)
            }
        }

    }
}