
import model.ChapterImages
import model.Chapters
import model.Manga

class Cache {
    val chapters = mutableMapOf<String, Chapters?>()

    val chapterImages = mutableMapOf<String, ChapterImages>()

    val mangaStatus = mutableMapOf<String, Manga>()
}