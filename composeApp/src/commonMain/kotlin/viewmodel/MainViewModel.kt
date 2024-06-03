package viewmodel

import Libs
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.getCoverUrl
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import util.LATEST_UPDATE_SLIDE_TIME
import util.SPLASH_TIME

class MainViewModel(private val mangaDex: MangaDex = Libs.mangaDex) : ViewModel() {
    private var executeOnce = mutableStateOf(true)

    private var _latestUpdateSlideTime = mutableStateOf(SPLASH_TIME + LATEST_UPDATE_SLIDE_TIME)
    val latestUpdateSlideTime = _latestUpdateSlideTime

    private var _currentPage = mutableStateOf(Page.MAIN)
    val currentPage = _currentPage

    val initialLatestUpdates = mutableStateListOf<Data<MangaAttributes>>()

    val latestUpdatesImages = mutableStateListOf<@Composable (ContentScale, Modifier) -> Unit>()

    fun setPage(page: Page) {
        _currentPage.value = page
    }

    private suspend fun initLatestUpdates() {
        initialLatestUpdates.addAll(mangaDex.getManga(generateArrayQueryParam(
            name = "includes[]",
            values = listOf("cover_art")
        ))?.data ?: listOf())
    }

    @Composable
    private fun initLatestUpdatesPainter() {
        initialLatestUpdates.forEach { data ->
            latestUpdatesImages.add { cs, m ->
                AutoSizeBox(getCoverUrl(data)) {action ->
                    when (action) {
                        is ImageAction.Success -> {
                            Image(
                                rememberImageSuccessPainter(action),
                                "cover art",
                                contentScale = cs,
                                modifier = m
                            )
                        }
                        is ImageAction.Loading -> Box(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                        else -> Text("Fail to load image", color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    fun init() {
        LaunchedEffect(true) {
            initLatestUpdates()
        }
        if (initialLatestUpdates.isNotEmpty() && executeOnce.value) {
            initLatestUpdatesPainter()
            executeOnce.value = false
        }
    }

    fun adjustLatestUpdatesSlideTime(new: Int) {
        if (_latestUpdateSlideTime.value != new) {
            _latestUpdateSlideTime.value = LATEST_UPDATE_SLIDE_TIME
        }
    }

    fun getTitle(title: Map<String, String>): String {
        return title["en"] ?: return title["ja"] ?: ""
    }

    fun getDesc(desc: Map<String, String>): String {
        return desc["en"] ?: desc["id"] ?: desc["ja"] ?: ""
    }
}

enum class Page {
    MAIN, FEED, DISCOVERY, USER_LIST
}