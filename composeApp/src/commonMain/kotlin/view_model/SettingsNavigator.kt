package view_model

import cafe.adriel.voyager.navigator.Navigator
import view.SettingsScreen

interface SettingsNavigator {
    fun navigateToSettings(nav: Navigator) {
        nav.push(SettingsScreen())
    }
}