package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import model.Manga
import screenSize
import view.ChapterScreen

@Serializable
data class DetailScreenModel(
    val manga: Manga
) : ScreenModel {
    var titleTagsPadding by mutableStateOf(16)
    var isShowingDetail by mutableStateOf(false)
    var chapterListHeight by mutableStateOf(0)
    var popNoticeWidth: Float by mutableStateOf(-(screenSize.width.value / 2f))
    private var animateOnce = false

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
}