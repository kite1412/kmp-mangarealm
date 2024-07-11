package view_model

import SharedObject
import cafe.adriel.voyager.navigator.Navigator
import model.ChapterList
import view.ReaderScreen

interface ReaderNavigator {
    fun navigateToReader(
        nav: Navigator,
        chapterList: ChapterList
    ) {
        SharedObject.chapterList = chapterList
        nav.push(ReaderScreen())
    }
}