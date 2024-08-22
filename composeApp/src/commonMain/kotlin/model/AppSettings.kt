package model

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf

data class AppSettings(
    var isDarkMode: MutableState<Boolean> = mutableStateOf(false),
    var enableSwipeToPop: MutableState<Boolean> = mutableStateOf(true),
    var swipeSensitivityFactor: MutableFloatState = mutableFloatStateOf(0f),
    var initialized: MutableState<Boolean> = mutableStateOf(false)
)