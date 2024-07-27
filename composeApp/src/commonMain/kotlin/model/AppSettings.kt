package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class AppSettings(
    var isDarkMode: MutableState<Boolean> = mutableStateOf(false),
    var enableSwipeToPop: MutableState<Boolean> = mutableStateOf(true),
    var initialized: MutableState<Boolean> = mutableStateOf(false)
)