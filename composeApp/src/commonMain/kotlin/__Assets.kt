
import androidx.compose.ui.graphics.vector.ImageVector
import assets.`Book-open-outline`
import assets.`Book-open-solid`
import assets.`Book-outline`
import assets.`Book-solid`
import assets.Eye
import assets.Eyeshut
import assets.Logo
import assets.Search
import assets.`Shelf-outline`
import assets.`Shelf-solid`
import kotlin.collections.List as ____KtList

public object Assets

private var __AllIcons: ____KtList<ImageVector>? = null

public val Assets.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(`Book-open-outline`, `Book-open-solid`, `Book-outline`, `Book-solid`, Eye,
        Eyeshut, Logo, Search, `Shelf-outline`, `Shelf-solid`)
    return __AllIcons!!
  }
