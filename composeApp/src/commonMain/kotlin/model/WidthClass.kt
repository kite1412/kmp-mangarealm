package model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed interface WidthClass {
    data object Compact : WidthClass
    data object Medium : WidthClass
    data object Expanded : WidthClass

    companion object {
        operator fun invoke(screenSize: ScreenSize): WidthClass = when (screenSize.width) {
            in Dp.Hairline..599.dp -> Compact
            in 600.dp..839.dp -> Medium
            else -> Expanded
        }
    }
}