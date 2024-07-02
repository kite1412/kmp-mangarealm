package viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import model.Manga
import screenSize
import view.ChapterScreen

@Serializable
data class DetailViewModel(
    val manga: Manga
) : ViewModel() {
    private var executeOnce = false
    var titleTagsPadding by mutableStateOf(16)
    var isShowingDetail by mutableStateOf(false)
    var chapterListHeight by mutableStateOf(0)
    var popNoticeWidth: Float by mutableStateOf(-(screenSize.width.value / 2f))
    private var animateOnce = false

    @Composable
    fun init(block: @Composable () -> Unit) {
        if (!executeOnce) {
            block()
            executeOnce = true
        }
    }

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
            viewModelScope.launch {
                popNoticeWidth = 0f
                delay(1000)
                popNoticeWidth = -(noticeWidth + additionalWidth.value)
            }
            animateOnce = true
        }
    }
}