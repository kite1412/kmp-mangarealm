package view_model

import Libs
import api.mangadex.service.MangaDex
import cafe.adriel.voyager.core.model.ScreenModel

class CustomListScreenModel(
    private val mangaDex: MangaDex = Libs.mangaDex
) : ScreenModel {

}