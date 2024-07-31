package view_model

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.viewModelScope
import api.mangadex.service.MangaDex
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Chapter
import model.ChapterImage
import model.ChapterImages
import util.ImageQuality
import util.retry

class ReaderScreenModel(
    private val sharedViewModel: SharedViewModel,
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache
) : ScreenModel {
    val chapterList = SharedObject.chapterList
    var chapters = chapterList()
    var currentChapterIndex by mutableIntStateOf(SharedObject.chapterList.index)
    var imageQuality by mutableStateOf("")
    var showPrompt by mutableStateOf(true)
    var zoomIn by mutableStateOf(true)
    var currentPage by mutableIntStateOf(1)
    var totalPages by mutableIntStateOf(0)
    var defaultLayout by mutableStateOf(true) // column
    var showLayoutBar by mutableStateOf(LayoutBarStatus.SHOW)
    var showPageIndicator by mutableStateOf(false)
    var layoutBarDismissible by mutableStateOf(true)
    var showPageNavigator by mutableStateOf(false)
    var pageNavigatorIndex by mutableIntStateOf(0)
    val images = mutableStateListOf<ChapterImage>()
    var index by mutableStateOf("")
    var showChapterList by mutableStateOf(false)
    var showWarning by mutableStateOf(false)
    var updateFromNavigator = false
    private var enableChapterNavigation = false
    private val manga = SharedObject.detailManga

    init {
        screenModelScope.launch {
            delay(100)
            showPrompt = true
            if (!chapterList.ascending) chapters = adjustDescendingChapter()
        }
    }

    private fun adjustDescendingChapter(): List<Chapter> = mutableListOf<Chapter>().apply {
        val start = chapters.subList(currentChapterIndex, chapters.size).reversed()
        val end = chapters.subList(0, currentChapterIndex).reversed()
        addAll(start + end)
        currentChapterIndex = start.size - 1
    }

    private fun getChapterImages() {
        screenModelScope.launch {
            showWarning = false
            val chapterId = chapters[currentChapterIndex]().id
            index = chapterId + imageQuality
            val images = cache.chapterImages[index]
            val res = retry(
                count = 3,
                predicate = { it == null || it.errors != null }
            ) {
                mangaDex.getHomeUrl(chapters[currentChapterIndex]().id)
            }
            if (images == null) {
                if (res != null) {
                    if (res.chapter.data.isEmpty()) showWarning = true
                    val imageFiles = if (imageQuality == ImageQuality.HIGH) res.chapter.data
                        else res.chapter.dataSaver
                    totalPages = imageFiles.size
                    val chapterImages = ChapterImages(
                        images = imageFiles.map { s ->
                            ChapterImage(
                                baseUrl = res.baseUrl,
                                hash = res.chapter.hash,
                                quality = imageQuality,
                                fileUrl = s,
                                painter = null
                            )
                        }.toMutableList()
                    )
                    cache.chapterImages[index] = chapterImages
                    this@ReaderScreenModel.images.addAll(chapterImages())
                    sharedViewModel.viewModelScope.launch {
                        retry(
                            count = 3,
                            predicate = { false }
                        ) {
                            mangaDex.updateMangaReadMarkers(
                                mangaId = manga.data.id,
                                readIds = listOf(chapterId)
                            )
                        }.also {
//                            val index = if (chapterList.ascending) currentChapterIndex
//                                else (chapters.size - 1) - currentChapterIndex
                            if (it) sharedViewModel.chapterReadMarkers[
                                manga.data.id + chapterList.lang
                            ]?.add(chapterId)
                        }
                    }
                }
            } else {
                if (res != null) {
                    images().forEach {
                        if (it.painter == null) {
                            if (it.baseUrl != res.baseUrl) it.baseUrl = res.baseUrl
                            if (it.hash != res.chapter.hash) it.hash = res.chapter.hash
                        }
                        this@ReaderScreenModel.images.add(it)
                    }
                }
                totalPages = images().size
            }
            enableChapterNavigation = true
        }
    }

    fun onPromptClick(q: String) {
        showPrompt = false
        imageQuality = q
        getChapterImages()
    }

    private fun layoutBarOnUpdate() {
        when(showLayoutBar) {
            LayoutBarStatus.SHOW -> showLayoutBar = LayoutBarStatus.UPDATE
            LayoutBarStatus.UPDATE -> showLayoutBar = LayoutBarStatus.SHOW
            else -> Unit
        }
    }

    fun changeLayout(layout: Layout) {
        layoutBarOnUpdate()
        defaultLayout = layout == Layout.COLUMN
    }

    fun handleLayoutBar() {
        if (!showChapterList) {
            if (layoutBarDismissible) {
                showLayoutBar = when(showLayoutBar) {
                    LayoutBarStatus.SHOW, LayoutBarStatus.UPDATE -> LayoutBarStatus.HIDE
                    else -> LayoutBarStatus.SHOW
                }
            } else {
                if (showPageNavigator) {
                    handlePageNavigator(showNavigator = false)
                    showLayoutBar = LayoutBarStatus.HIDE
                }
            }
        } else {
            showChapterList = false
        }
    }

    fun handlePageNavigator(showNavigator: Boolean) {
        showPageNavigator = showNavigator
        this.layoutBarDismissible = !showNavigator
    }

    fun updatePainter(index: Int, p: Painter?) {
        images[index] = images[index].copy(painter = p)
        cache.chapterImages[this.index]!!.images[index].painter = p
    }

    fun onChapterClick(index: Int) {
        if (enableChapterNavigation) {
            enableChapterNavigation = false
            showChapterList = false
            currentChapterIndex = index
            currentPage = 1
            pageNavigatorIndex = 0
            this.index = chapters[currentChapterIndex]().id + imageQuality
            images.clear()
            getChapterImages()
        }
    }

    suspend fun togglePageIndicator() {
        showPageIndicator = true
        delay(1000)
        showPageIndicator = false
    }
}

enum class LayoutBarStatus {
    SHOW,
    HIDE,
    UPDATE
}

enum class Layout {
    COLUMN,
    ROW;

    override fun toString(): String =
        if (this == COLUMN) "Column" else "Row"
}