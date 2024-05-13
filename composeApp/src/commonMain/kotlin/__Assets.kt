import androidx.compose.ui.graphics.vector.ImageVector
import assets.`Full-logo`
import kotlin.collections.List as ____KtList

public object Assets

private var __AllIcons: ____KtList<ImageVector>? = null

public val Assets.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(`Full-logo`)
    return __AllIcons!!
  }
