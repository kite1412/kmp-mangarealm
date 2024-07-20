package view_model

import Libs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.CustomList
import model.session.CustomListSession
import model.session.isEmpty
import model.toCustomList
import util.WARNING_TIME
import util.retry

class CustomListScreenModel(
    val sharedViewModel: SharedViewModel,
    private val mangaDex: MangaDex = Libs.mangaDex
) : ScreenModel {
    var showPopNotice by mutableStateOf(false)
    var showWarning by mutableStateOf(false)
    var showUpdateLoading by mutableStateOf(false)
    var loadingMessage = ""
    var warningMessage = ""
    var deleteCount = 0

    init {
        beginSession()
    }

    override fun onDispose() {
        sharedViewModel.viewModelScope.launch {
            sharedViewModel.deleteDeletedCustomList()
        }
    }

    private fun beginSession() {
        screenModelScope.launch {
            val session = sharedViewModel.customListSession
            if (session.isEmpty()) {
                sharedViewModel.customListSession.init(session.queries)
                val res = retry(
                    count = 3,
                    predicate = { it == null || it.errors != null }
                ) {
                    mangaDex.getUserCustomLists(generateQuery(session.queries))
                }
                if (res != null) {
                    val new = CustomListSession().apply {
                        setActive(
                            response = res,
                            data = res.data.map { it.toCustomList() }
                        )
                    }
                    sharedViewModel.updateCustomListSession(new)
                }
            }
        }
    }

    private suspend fun showWarning(message: String) {
        warningMessage = message
        showWarning = true
        delay(WARNING_TIME)
        showWarning = false
    }

    private fun showUpdateLoading(message: String) {
        loadingMessage = message
        showUpdateLoading = true
    }

    fun deleteCustomList(customList: CustomList, index: Int) {
        sharedViewModel.viewModelScope.launch {
            showUpdateLoading("Deleting list...")
            val success = retry(
                count = 3,
                predicate = { false }
            ) {
                mangaDex.deleteCustomList(customList.data.id)
            }
            if (success) {
                sharedViewModel.customListSession.data[index] =
                    sharedViewModel.customListSession.data[index].copy(deleted = true)
                launch { showWarning("List deleted") }
                deleteCount++
                if (deleteCount == sharedViewModel.customListSession.data.size)
                    sharedViewModel.deleteDeletedCustomList()
            } else {
                launch { showWarning("Failed to delete list") }
            }
            showUpdateLoading = false
        }
    }
}