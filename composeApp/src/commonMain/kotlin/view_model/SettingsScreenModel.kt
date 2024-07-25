package view_model

import Libs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.put
import kotlinx.coroutines.launch
import util.KottageConst

class SettingsScreenModel(
    private val sharedViewModel: SharedViewModel,
    private val kottageStorage: KottageStorage = Libs.kottageStorage
) : ScreenModel {
    var showPopNotice by mutableStateOf(false)
    var isDarkMode by mutableStateOf(false)

    init {
        isDarkMode = sharedViewModel.appSettings.isDarkMode.value
    }

    fun toggleDarkMode(b: Boolean) {
        screenModelScope.launch {
            isDarkMode = b
            sharedViewModel.appSettings.isDarkMode.value = b
            kottageStorage.put(KottageConst.THEME_MODE, b)
        }
    }
}