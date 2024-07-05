package viewmodel

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import api.mangadex.service.MangaDex
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch

class ReaderScreenModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache
) : ScreenModel {
    val chapter = SharedObject.chapterRead
    var imageQuality by mutableStateOf("")

    init {

    }

    private fun getChapterImages() {
        val images = cache.chapterImages[chapter.id]
        if (images == null) {
            screenModelScope.launch {
                val res = mangaDex.getHomeUrl(chapter.id)
                if (res != null) {

                }
            }
        } else {

        }
    }
}