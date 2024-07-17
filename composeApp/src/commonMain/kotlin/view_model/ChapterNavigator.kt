package view_model

import SharedObject
import cafe.adriel.voyager.navigator.Navigator
import model.Manga
import view.ChapterScreen

interface ChapterNavigator {
    fun navigateToChapter(nav: Navigator, manga: Manga? = null) {
        if (manga != null) SharedObject.detailManga = manga
        nav.push(ChapterScreen())
    }
}