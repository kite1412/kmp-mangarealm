package viewmodel

import SharedObject
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.ChapterAttributes
import cafe.adriel.voyager.navigator.Navigator
import view.ReaderScreen

interface ReaderNavigator {
    fun navigateToReader(
        nav: Navigator,
        chapter: Data<ChapterAttributes>
    ) {
        SharedObject.chapterRead = chapter
        nav.push(ReaderScreen())
    }
}