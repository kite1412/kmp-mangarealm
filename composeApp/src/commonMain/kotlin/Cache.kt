
import model.ChapterImages
import model.Chapters
import model.Manga
import model.Session

class Cache {
    val chapters = mutableMapOf<String, Chapters?>()

    val chapterImages = mutableMapOf<String, ChapterImages>()

    val mangaStatus = mutableMapOf<String, Manga>()

    val latestMangaSearch = mutableMapOf<String, Session>()
}