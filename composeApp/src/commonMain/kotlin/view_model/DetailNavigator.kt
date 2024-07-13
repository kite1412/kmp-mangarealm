package view_model

import SharedObject
import cafe.adriel.voyager.navigator.Navigator
import model.Manga
import view.DetailScreen

/** Should only be implemented by ViewModel that could navigate to [DetailScreen] */
interface DetailNavigator {
    fun navigateToDetail(nav: Navigator, manga: Manga) {
        SharedObject.detailManga = manga
        nav.push(DetailScreen())
    }
}