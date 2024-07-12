import androidx.compose.ui.graphics.vector.ImageVector
import assets.Clipboard
import assets.Cross
import assets.Eye
import assets.Eyeshut
import assets.Height
import assets.Home
import assets.Info
import assets.Logo
import assets.Person
import assets.Search
import assets.Width
import assets.`Arrow-left-solid`
import assets.`Book-close`
import assets.`Book-open`
import assets.`Bookmark-alt-fill`
import assets.`Bookmark-alt`
import assets.`Chevron-right-bold`
import assets.`Chevron-right`
import assets.`Collapse-solid`
import assets.`Expand-solid`
import assets.`List-add`
import assets.`List-check`
import assets.`Menu-outline`
import assets.`Settings-adjust-solid`
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
    __AllIcons= listOf(`Arrow-left-solid`, `Book-close`, `Book-open`, `Bookmark-alt-fill`,
        `Bookmark-alt`, `Chevron-right-bold`, `Chevron-right`, Clipboard, `Collapse-solid`, Cross,
        `Expand-solid`, Eye, Eyeshut, Height, Home, Info, `List-add`, `List-check`, Logo,
        `Menu-outline`, Person, Search, `Settings-adjust-solid`, `Shelf-outline`, `Shelf-solid`,
        `Text-align-right`, Width)
    return __AllIcons!!
  }
