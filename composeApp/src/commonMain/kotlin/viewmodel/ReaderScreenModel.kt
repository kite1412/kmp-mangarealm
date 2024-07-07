package viewmodel

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import api.mangadex.service.MangaDex
import api.mangadex.util.getChapterImageUrls
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.ImageQuality

class ReaderScreenModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache
) : ScreenModel {
    val chapter = SharedObject.chapterRead
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
    val imageFiles = mutableStateListOf<String>()
    val painters = mutableStateListOf<Painter?>()

    init {
        screenModelScope.launch {
            delay(100)
            showPrompt = true
        }
    }

    private fun getChapterImages() {
        val images = cache.chapterImages[chapter.id]
        if (images == null) {
            screenModelScope.launch {
                val res = mangaDex.getHomeUrl(chapter.id)
                if (res != null) {
                    // TODO handle caching
                    getChapterImageUrls(res, ImageQuality(imageQuality)).run {
                        totalPages = size
                        forEach {
                            imageFiles.add(it)
                            painters.add(null)
                        }
                    }
                }
            }
        } else {
            painters.addAll(images())
        }
    }

    fun onPromptClick(q: String) {
        showPrompt = false
        when(q) {
            ImageQuality.HIGH -> imageQuality = ImageQuality.HIGH
            ImageQuality.DATA_SAVER -> imageQuality = ImageQuality.DATA_SAVER
        }
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
        defaultLayout = when(layout) {
            Layout.COLUMN -> true
            Layout.ROW -> false
        }
    }

    fun handleLayoutBar() {
        if (layoutBarDismissible) {
            showLayoutBar = when(showLayoutBar) {
                LayoutBarStatus.SHOW -> LayoutBarStatus.HIDE
                LayoutBarStatus.UPDATE -> LayoutBarStatus.HIDE
                else -> LayoutBarStatus.SHOW
            }
        } else {
            if (showPageNavigator) {
                handlePageNavigator(showNavigator = false, layoutBarDismissible = true)
                showLayoutBar = LayoutBarStatus.HIDE
            }
        }
    }

    fun handlePageNavigator(showNavigator: Boolean, layoutBarDismissible: Boolean) {
        showPageNavigator = showNavigator
        this.layoutBarDismissible = layoutBarDismissible
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

    override fun toString(): String = when(this) {
        COLUMN -> "Column"
        ROW -> "Row"
    }
}