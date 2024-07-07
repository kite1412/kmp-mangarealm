package model

data class ChapterImages(
    val images: MutableList<ChapterImage>
) {
    operator fun invoke(): MutableList<ChapterImage> = images
}
