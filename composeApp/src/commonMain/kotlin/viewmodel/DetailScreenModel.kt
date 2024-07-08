package viewmodel

import Cache
import Libs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ChapterList
import model.Chapters
import model.Manga
import screenSize
import util.ASCENDING
import view.ChapterScreen

class DetailScreenModel(
    val manga: Manga,
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache
) : ScreenModel, ReaderNavigator {
    var titleTagsPadding by mutableStateOf(16)
    var isShowingDetail by mutableStateOf(false)
    var chapterListHeight by mutableStateOf(0)
    var popNoticeWidth: Float by mutableStateOf(-(screenSize.width.value / 2f))
    private var animateOnce = false
    var readClicked by mutableStateOf(false)

    fun detailVisibility() {
        isShowingDetail = !isShowingDetail
    }

    fun navigateToChapterListScreen(nav: Navigator) {
        nav.push(ChapterScreen())
    }

    fun animatePopNotice(
        noticeWidth: Float,
        additionalWidth: Dp
    ) {
        if (!animateOnce) {
            screenModelScope.launch {
                popNoticeWidth = 0f
                delay(1000)
                popNoticeWidth = -(noticeWidth + additionalWidth.value)
            }
            animateOnce = true
        }
    }

    fun onReadClick(nav: Navigator) {
        readClicked = true
        screenModelScope.launch {
            val chapters = cache.chapters[manga.data.id]
            if (chapters == null) {
                val languages = manga.data.attributes
                    .availableTranslatedLanguages.filterNotNull().filter {
                        it == "en" || it == "id"
                    }
                var l = ""
                if (languages.isNotEmpty()) {
                    for (lang in languages) {
                        if (lang == "en") {
                            l = lang
                            break
                        }
                        l = lang
                    }
                } else {
                    l =  manga.data.attributes.availableTranslatedLanguages[0]!!
                }
                val lang = generateQuery(mapOf("translatedLanguage[]" to l))
                val res = mangaDex.getMangaChapters(
                    mangaId = manga.data.id,
                    queries = generateQuery(
                        queryParams = mapOf("order[chapter]" to ASCENDING),
                        otherParams = lang
                    )
                )
                if (res != null) {
                    val chapterList = ChapterList(chapters = res.data)
                    cache.chapters[manga.data.id] = Chapters(
                        language = l,
                        order = ASCENDING,
                        response = res,
                        data = res.data.toMutableList()
                    )
                    if (res.data.isNotEmpty()) navigateToReader(
                        nav = nav,
                        chapterList = chapterList
                    )
                }
            } else navigateToReader(
                nav = nav,
                chapterList = ChapterList(chapters = chapters())
            )
            readClicked = false
        }
    }
}