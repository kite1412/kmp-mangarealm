package view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel

class SettingsScreenModel : ScreenModel {
    var showPopNotice by mutableStateOf(false)
    var isDarkMode by mutableStateOf(false)

    fun toggleDarkMode(b: Boolean) {
        isDarkMode = b
    }
}