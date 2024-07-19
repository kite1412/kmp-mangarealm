
import model.ChapterImages
import model.Chapters
import model.session.MangaSession

// runtime cache
class Cache {
    val chapters = mutableMapOf<String, Chapters?>()

    val chapterImages = mutableMapOf<String, ChapterImages>()

    val latestMangaSearch = mutableMapOf<String, MangaSession>()
}