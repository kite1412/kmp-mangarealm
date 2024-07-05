
import model.ChapterImages
import model.Chapters

class Cache {
    val chapters =
        mutableMapOf<String, Chapters?>()

    val chapterImages =
        mutableMapOf<String, ChapterImages>()
}