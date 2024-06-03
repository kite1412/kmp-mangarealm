import androidx.compose.ui.graphics.vector.ImageVector
import assets.Clipboard
import assets.Eye
import assets.Eyeshut
import assets.Home
import assets.Logo
import assets.Person
import assets.Search
import assets.`Book-close`
import assets.`Book-open-outline`
import assets.`Book-open-solid`
import assets.`Book-outline`
import assets.`Book-solid`
import assets.`Shelf-outline`
import assets.`Shelf-solid`
import assets.`Text-align-right`
import kotlin.collections.List as ____KtList

public object Assets

private var __AllIcons: ____KtList<ImageVector>? = null

public val Assets.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(`Book-close`, `Book-open-outline`, `Book-open-solid`, `Book-outline`,
        `Book-solid`, Clipboard, Eye, Eyeshut, Home, Logo, Person, Search, `Shelf-outline`,
        `Shelf-solid`, `Text-align-right`)
    return __AllIcons!!
  }
