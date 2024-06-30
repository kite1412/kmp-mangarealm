package viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import model.Manga

class DetailViewModel(
    val manga: Manga
) : ViewModel() {
    private var executeOnce = false
    var titleTagsPadding = mutableStateOf(16.dp)

    @Composable
    fun init(block: @Composable () -> Unit) {
        if (!executeOnce) {
            block()
            executeOnce = true
        }
    }
}