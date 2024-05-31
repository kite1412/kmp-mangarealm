package viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import api.mangadex.util.coverImageUrl
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

class MainViewModel : ViewModel() {
    private var _latestUpdatesCurrentResource: MutableState<Resource<Painter>?> = mutableStateOf(null)
    val latestUpdatesCurrentResource = _latestUpdatesCurrentResource

    private var _executeOnce = mutableStateOf(true)
    val executeOnce = _executeOnce

    @Composable
    fun init() {
        _latestUpdatesCurrentResource.value = asyncPainterResource(data = coverImageUrl(
            mangaId = "05bd710c-d94a-45eb-be99-2109d58f1006",
            filename = "bc22bc4a-5d83-40f8-85bd-45b7ecb08d83.jpg"
        ))
    }

    fun setExecuteOnce(new: Boolean) {
        _executeOnce.value = new
    }

    fun getTitle(title: Map<String, String>): String {
        return title["en"] ?: return title["ja"]!!
    }
}