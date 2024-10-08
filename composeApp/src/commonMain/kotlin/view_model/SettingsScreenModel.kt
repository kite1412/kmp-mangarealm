package view_model

import Libs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    var enableSwipeToPop by mutableStateOf(true)
    var swipeSensitivityFactor by mutableFloatStateOf(0f)

    init {
        isDarkMode = sharedViewModel.appSettings.isDarkMode.value
        enableSwipeToPop = sharedViewModel.appSettings.enableSwipeToPop.value
        swipeSensitivityFactor = sharedViewModel.appSettings.swipeSensitivityFactor.value
    }

    fun toggleDarkMode(b: Boolean) {
        screenModelScope.launch {
            isDarkMode = b
            sharedViewModel.appSettings.isDarkMode.value = b
            kottageStorage.put(KottageConst.THEME_MODE, b)
        }
    }

    fun toggleSwipeToPop(b: Boolean) {
        screenModelScope.launch {
            enableSwipeToPop = b
            sharedViewModel.appSettings.enableSwipeToPop.value = b
            kottageStorage.put(KottageConst.SWIPE_TO_POP, b)
        }
    }

    fun newSwipeSensitivityFactor(factor: Float) {
        swipeSensitivityFactor = factor
    }

    fun saveSwipeSensitivityFactor() {
        screenModelScope.launch {
            sharedViewModel.appSettings.swipeSensitivityFactor.value = swipeSensitivityFactor
            kottageStorage.put(KottageConst.SWIPE_SENSITIVITY_FACTOR, swipeSensitivityFactor)
        }
    }
}