package view_model

import SharedObject
import cafe.adriel.voyager.navigator.Navigator
import model.Manga
import view.DetailScreen

interface DetailNavigator {
    val sharedViewModel: SharedViewModel

    fun navigateToDetail(nav: Navigator, manga: Manga) {
        SharedObject.detailManga = sharedViewModel.findMangaStatus(manga) ?: manga
        nav.push(DetailScreen())
    }
}