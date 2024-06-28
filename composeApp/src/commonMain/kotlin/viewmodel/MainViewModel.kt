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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.Status
import api.mangadex.util.constructQuery
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import api.mangadex.util.getCoverUrl
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import util.ADVENTURE_TAG
import util.COMEDY_TAG
import util.KottageConst
import util.MYSTERY_TAG
import util.PSYCHOLOGICAL_TAG
import util.ROMANCE_TAG

class MainViewModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val kottageStorage: KottageStorage = Libs.kottageStorage
) : ViewModel() {
    private var _currentPage = mutableStateOf(Page.MAIN)
    val currentPage = _currentPage

    private var executeOnce = false

    val initialLatestUpdatesData = mutableStateListOf<Data<MangaAttributes>>()
    val continueReadingData = mutableStateListOf<Data<MangaAttributes>>()

    private var _username = mutableStateOf("")
    val username = _username

    val romcomManga = mutableStateListOf<Data<MangaAttributes>>()
    val advComManga = mutableStateListOf<Data<MangaAttributes>>()
    val psyMysManga = mutableStateListOf<Data<MangaAttributes>>()

    val latestUpdatesCovers = mutableStateListOf<@Composable (
        ContentScale,
        Modifier
    ) -> Unit>()

    val latestUpdatesPainterSB = mutableStateListOf<Painter?>()

    val continueReadingCovers = mutableStateListOf<@Composable (
        ContentScale,
        Modifier
    ) -> Unit>()

    fun setPage(page: Page) {
        _currentPage.value = page
    }

    suspend fun updateUsername() {
        val username = kottageStorage.get<String>(KottageConst.USERNAME)
        _username.value = username
    }

    private suspend fun initLatestUpdates() {
        initialLatestUpdatesData.addAll(mangaDex.getManga(generateArrayQueryParam(
            name = "includes[]",
            values = listOf("cover_art")
        ))?.data ?: listOf())
    }

    private fun initLatestUpdatesCovers() {
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

    @Composable
    private fun initLatestUpdatesPainterSB() {
        for (i in 0 until 10) {
            latestUpdatesPainterSB.add(null)
        }
        initialLatestUpdatesData.forEachIndexed { i, d ->
            AutoSizeBox(getCoverUrl(d)) {action ->
                when (action) {
                    is ImageAction.Success -> {
                        latestUpdatesPainterSB[i] = rememberImageSuccessPainter(action)
                    }
                    else -> Unit
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

    private suspend fun fetchByTags(
        tags: List<String>,
        onSuccess: (List<Data<MangaAttributes>>) -> Unit,
        excludedTags: List<String> = listOf()
    ) {
        val tagsParam = generateArrayQueryParam(
            name = "includedTags[]",
            values = tags
        )
        val excludedTagsParam = generateArrayQueryParam(
            name = "excludedTags[]",
            values = excludedTags
        )
        val temp = generateQuery(
            queryParams = mapOf("includes[]" to "cover_art"),
            otherParams = tagsParam
        )
        val queries = constructQuery(excludedTagsParam, temp)
        val res = mangaDex.getManga(queries)
        if (res != null) {
            if (res.data.isNotEmpty()) {
                onSuccess(res.data)
            }
        }
    }

    suspend fun fetchMangaByTags(tags: Map<String, String>) {
        fetchByTags(
            tags = listOf(tags[ROMANCE_TAG]!!, tags[COMEDY_TAG]!!),
            onSuccess = {
                romcomManga.addAll(it)
            },
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[ADVENTURE_TAG]!!, tags[COMEDY_TAG]!!),
            onSuccess = {
                advComManga.addAll(it)
            },
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!),
            onSuccess = {
                psyMysManga.addAll(it)
            },
            excludedTags = listOf(tags[COMEDY_TAG]!!)
        )
    }

    @Composable
    fun init() {
        if (!executeOnce) {
            LaunchedEffect(true) {
                initLatestUpdates()
                if (initialLatestUpdatesData.isNotEmpty()) {
                    initLatestUpdatesCovers()
                }
                initContinueReading()
            }
            initLatestUpdatesPainterSB()
            executeOnce = true
        }
    }
}

enum class Page {
    MAIN, FEED, DISCOVERY, USER_LIST
}