package viewmodel

import Libs
import SharedObject
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import kotlinx.coroutines.launch

class ChapterViewModel(
    private val mangaDex: MangaDex = Libs.mangaDex
) : ViewModel() {
    val chapters = mutableStateListOf<Data<ChapterAttributes>>()
    val availableLanguages = mutableStateListOf<String>()

    init {
        fetchChapters()
    }

    private fun getByLanguage(
        languages: List<String>,
        vararg prioritizedLanguage: String = arrayOf("en", "id")
    ): String {
        languages.forEach { language ->
            if (language in prioritizedLanguage) return language
        }
        return languages[0]
    }

    private fun fetchChapters() {
        viewModelScope.launch {
            val manga = SharedObject.detailManga
            manga.data.attributes.availableTranslatedLanguages.let {
                it.forEach { s ->
                    if (s != null) availableLanguages.add(s)
                }
            }
            val language = generateArrayQueryParam(
                "translatedLanguage[]",
                listOf(getByLanguage(availableLanguages))
            )
            val response = mangaDex.getMangaChapters(
                mangaId = manga.data.id,
                queries = generateQuery(
                    queryParams = mapOf("order[chapter]" to "asc"),
                    otherParams = language
                )
            )
            if (response != null) chapters.addAll(response.data)
        }
    }
}