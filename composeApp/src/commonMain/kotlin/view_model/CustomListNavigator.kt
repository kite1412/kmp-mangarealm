package view_model

import cafe.adriel.voyager.navigator.Navigator
import view.CustomListScreen

interface CustomListNavigator {
    fun navigateToCustomList(nav: Navigator) {
        nav.push(CustomListScreen())
    }
}