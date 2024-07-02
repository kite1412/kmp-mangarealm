package viewmodel

import SharedObject
import androidx.compose.ui.graphics.painter.Painter
import cafe.adriel.voyager.navigator.Navigator
import model.Manga
import view.DetailScreen

/** Should only be implemented by ViewModel that could navigate to [DetailScreen] */
interface DetailNavigable {
    fun navigateToDetail(nav: Navigator, painter: Painter?, manga: Manga) {
        if (painter != null) {
            SharedObject.detailCover = painter
            SharedObject.detailManga = manga
            nav.push(DetailScreen())
        }
    }
}