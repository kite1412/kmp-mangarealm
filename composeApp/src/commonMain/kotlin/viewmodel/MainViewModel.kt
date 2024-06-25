package viewmodel

import Libs
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.Status
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import api.mangadex.util.getCoverUrl
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import util.LATEST_UPDATE_SLIDE_TIME
import util.SPLASH_TIME

class MainViewModel(private val mangaDex: MangaDex = Libs.mangaDex) : ViewModel() {
    private var executeOnce = mutableStateOf(true)

    private var _latestUpdateSlideTime = mutableStateOf(SPLASH_TIME + LATEST_UPDATE_SLIDE_TIME)
    val latestUpdateSlideTime = _latestUpdateSlideTime

    private var _currentPage = mutableStateOf(Page.MAIN)
    val currentPage = _currentPage

    val initialLatestUpdatesData = mutableStateListOf<Data<MangaAttributes>>()
    val continueReadingData = mutableStateListOf<Data<MangaAttributes>>()

    val latestUpdatesCovers = mutableStateListOf<@Composable (
        ContentScale,
        Modifier
    ) -> Unit>()

    val continueReadingCovers = mutableStateListOf<@Composable (
        ContentScale,
        Modifier
    ) -> Unit>()

    fun setPage(page: Page) {
        _currentPage.value = page
    }

    private suspend fun initLatestUpdates() {
        initialLatestUpdatesData.addAll(mangaDex.getManga(generateArrayQueryParam(
            name = "includes[]",
            values = listOf("cover_art")
        ))?.data ?: listOf())
    }

    private fun initLatestUpdatesPainter() {
        initialLatestUpdatesData.forEach { data ->
            latestUpdatesCovers.add { cs, m ->
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
                        is ImageAction.Loading -> {
                            val state = remember { HazeState() }
                            Box(Modifier.fillMaxSize().haze(state)) {
                                Box(Modifier.fillMaxSize().hazeChild(state))
                            }
                        }
                        else -> Text("Fail to load image", color = Color.White)
                    }
                }
            }
        }
    }

    private suspend fun initContinueReading() {
        val res = mangaDex.getMangaByStatus(Status.READING)
        if (res?.statuses != null) {
            if (res.statuses.isNotEmpty()) {
                val temp = mutableListOf<String>()
                res.statuses.forEach { manga ->
                    temp.add(manga.key)
                }
                val mangaIds = generateArrayQueryParam(
                    name = "ids[]",
                    values = temp
                )
                val queries = generateQuery(mapOf(Pair("includes[]", "cover_art")), mangaIds)
                val mangaList = mangaDex.getManga(queries)
                mangaList?.data?.let { continueReadingData.addAll(it) }
                mangaList?.data?.forEach { manga ->
                    continueReadingCovers.add { cs, m ->
                        AutoSizeBox(getCoverUrl(manga)) { action ->
                            when (action) {
                                is ImageAction.Success -> {
                                    Image(
                                        rememberImageSuccessPainter(action),
                                        "cover art",
                                        contentScale = cs,
                                        modifier = m
                                    )
                                }
                                is ImageAction.Loading -> {
                                    Box(modifier = m) {
                                        CircularProgressIndicator(
                                            strokeWidth = 2.dp,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .size(14.dp)
                                        )
                                    }
                                }
                                else -> Text("No Image")
                            }
                        }
                    }
                }
            } else return
        }
    }

    @Composable
    fun init() {
        LaunchedEffect(true) {
            initLatestUpdates()
            if (initialLatestUpdatesData.isNotEmpty()) {
                initLatestUpdatesPainter()
            }
            initContinueReading()
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