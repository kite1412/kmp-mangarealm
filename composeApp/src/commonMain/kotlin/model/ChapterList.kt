package model

data class ChapterList(
    val index: Int = 0,
    val chapters: List<Chapter> = listOf()
) {
    operator fun invoke(): List<Chapter> = chapters
}
