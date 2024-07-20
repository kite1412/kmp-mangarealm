package view_model

import Libs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import model.session.CustomListSession
import model.session.isEmpty
import util.retry

class CustomListScreenModel(
    private val sharedViewModel: SharedViewModel,
    private val mangaDex: MangaDex = Libs.mangaDex
) : ScreenModel {
    var showPopNotice by mutableStateOf(false)
    val session = sharedViewModel.customListSession

    init {
        beginSession()
    }

    private fun beginSession() {
        screenModelScope.launch {
            if (session.isEmpty()) {
                session.init(session.queries)
                val res = retry(
                    count = 3,
                    predicate = { it == null || it.errors != null }
                ) {
                    mangaDex.getUserCustomLists(generateQuery(session.queries))
                }
                if (res != null) {
                    val new = CustomListSession().apply {
                        setActive(res, res.data)
                    }
                    session.from(new)
                    sharedViewModel.updateCustomListSession(new)
                }
            }
        }
    }
}