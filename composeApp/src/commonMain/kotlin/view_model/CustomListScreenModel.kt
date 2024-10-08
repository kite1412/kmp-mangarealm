package view_model

import Libs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import api.mangadex.model.request.CreateCustomList
import api.mangadex.model.request.Visibility
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.CustomList
import model.Manga
import model.toCustomList
import model.toMangaList
import util.WARNING_TIME
import util.retry

class CustomListScreenModel(
    override val sharedViewModel: SharedViewModel,
    private val mangaDex: MangaDex = Libs.mangaDex
) : ScreenModel, DetailNavigator {
    var showPopNotice by mutableStateOf(false)
    var showWarning by mutableStateOf(false)
    var showUpdateLoading by mutableStateOf(false)
    var loadingMessage = ""
    var warningMessage = ""
    private var deleteCount = 0
    var textFieldValue by mutableStateOf(TextFieldValue())
    var visibility by mutableStateOf(Visibility.PRIVATE)
    var showAddPrompt by mutableStateOf(false)
    var selectedCustomListIndex = 0
    var isOnEditMode = false
    private var editCustomList: CustomList? = null
    val swipeToPopEnabled by sharedViewModel.appSettings.enableSwipeToPop

    init {
        sharedViewModel.beginCustomListSession()
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
        textFieldValue = TextFieldValue()
        visibility = Visibility.PRIVATE
        action()
    }

    fun deleteCustomList(customList: CustomList, index: Int) {
        sharedViewModel.viewModelScope.launch {
            showUpdateLoading("Deleting list...")
            val success = retry(
                maxAttempts = 3,
                predicate = { false }
            ) {
                mangaDex.deleteCustomList(customList.data.value.id)
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
            textFieldValue = TextFieldValue()
            visibility = Visibility.PRIVATE
        }
        showWarning(message)
    }

    fun onAddCustomList(customList: CustomList? = null) {
        isOnEditMode = false
        if (customList != null) {
            editCustomList = customList
            isOnEditMode = true
            val value = customList.data.value.attributes.name
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
            visibility = Visibility.fromString(customList.data.value.attributes.visibility)
        }
        showAddPrompt = true
    }

    fun addCustomList() {
        screenModelScope.launch {
            showUpdateLoading("Adding list...")
            val res = retry<EntityResponse<CustomListAttributes>?>(
                maxAttempts = 3,
                predicate = { it == null || it.errors != null }
            ) {
                mangaDex.createCustomList(
                    CreateCustomList(
                        name = textFieldValue.text,
                        visibility = visibility
                    )
                )
            }
            dismissLoading {
                textFieldValue = TextFieldValue()
                visibility = Visibility.PRIVATE
            }
            if (res != null) {
                sharedViewModel.customListSession.data.add(res.data!!.toCustomList())
                dismissAddPrompt("List added")
            } else dismissAddPrompt("Failed to add list")
        }
    }

    fun editCustomList() {
        sharedViewModel.viewModelScope.launch {
            showUpdateLoading("Updating list...")
            sharedViewModel.editCustomList(
                editCustomList!!,
                editCustomList!!.data.value.attributes.copy(
                    name = textFieldValue.text,
                    visibility = visibility.toString()
                )
            )
            dismissAddPrompt("List updated")
        }
    }

    fun fetchManga(index: Int) {
        selectedCustomListIndex = index
        screenModelScope.launch {
            val customList = sharedViewModel.customListSession.data[index]
            if (customList.mangaIds.size > 0 &&
                (customList.manga.isEmpty() || customList.manga.size != customList.mangaIds.size)) {
                val res = retry(
                    maxAttempts = 3,
                    predicate = { it == null || it.errors != null }
                ) {
                    val ids = generateArrayQueryParam("ids[]", customList.mangaIds)
                    val includes = generateQuery(mapOf("includes[]" to "cover_art"), ids)
                    mangaDex.getManga(includes)
                }
                if (res != null) {
                    val data = res.toMangaList()
                    sharedViewModel.updateCustomListManga(customList, data)
                }
            }
        }
    }

    fun deleteMangaFromList(customList: CustomList, manga: Manga) {
        sharedViewModel.removeCustomListManga(customList, manga)
        screenModelScope.launch { showWarning("Deleted") }
    }
}