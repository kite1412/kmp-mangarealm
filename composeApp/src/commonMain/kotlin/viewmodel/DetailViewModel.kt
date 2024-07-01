package viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import model.Manga

class DetailViewModel(
    val manga: Manga
) : ViewModel() {
    private var executeOnce = false
    var titleTagsPadding = mutableStateOf(16.dp)
    var isShowingDetail = mutableStateOf(false)
    var chapterListHeight = mutableStateOf(0)
    var popNoticeWidth by mutableStateOf(0)

    @Composable
    fun init(block: @Composable () -> Unit) {
        if (!executeOnce) {
            block()
            executeOnce = true
        }
    }

    fun detailVisibility() {
        isShowingDetail.value = !isShowingDetail.value
    }

    fun navigateToChapterListScreen() {

    }
}