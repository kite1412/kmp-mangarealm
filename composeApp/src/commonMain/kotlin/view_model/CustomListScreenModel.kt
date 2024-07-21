package view_model

import Libs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import api.mangadex.model.request.CreateCustomList
import api.mangadex.model.request.Visibility
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.service.MangaDex
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.CustomList
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
    private var deleteCount = 0
    var textFieldValue by mutableStateOf("")
    var visibility by mutableStateOf(Visibility.PRIVATE)
    var showAddPrompt by mutableStateOf(false)

    init {
        sharedViewModel.beginSession()
    }

    override fun onDispose() {
        sharedViewModel.viewModelScope.launch {
            sharedViewModel.deleteDeletedCustomList()
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

    private fun dismissLoading(action: () -> Unit) {
        showUpdateLoading = false
        action()
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
                if (deleteCount == sharedViewModel.customListSession.data.size) {
                    deleteCount = 0
                    sharedViewModel.deleteDeletedCustomList()
                }
            } else {
                launch { showWarning("Failed to delete list") }
            }
            dismissLoading {}
        }
    }

    private suspend fun dismissAddPrompt(message: String) {
        dismissLoading {
            showAddPrompt = false
            textFieldValue = ""
            visibility = Visibility.PRIVATE
        }
        showWarning(message)
    }

    fun onAdd() {
        screenModelScope.launch {
            showUpdateLoading("Adding list...")
            val res = retry<EntityResponse<CustomListAttributes>?>(
                count = 3,
                predicate = { it == null || it.errors != null }
            ) {
                mangaDex.createCustomList(CreateCustomList(
                    name = textFieldValue,
                    visibility = visibility
                ))
            }
            dismissLoading {
                textFieldValue = ""
                visibility = Visibility.PRIVATE
            }
            if (res != null) {
                sharedViewModel.customListSession.data.add(res.data!!.toCustomList())
                dismissAddPrompt("List added")
            } else dismissAddPrompt("Failed to add list")
        }
    }
}