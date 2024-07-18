package view_model.main.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.Status
import view_model.main.MainViewModel

class UserListState(
    vm: MainViewModel,
    private val scope: CoroutineScope = vm.viewModelScope
) {
    var selectedStatus by mutableStateOf(MangaStatus.None)
    var showOptions by mutableStateOf(false)
    val manga = mutableStateListOf<Manga>()

    fun onStatusSelected(new: Status) {
        scope.launch {
            selectedStatus = new
            delay(300)
            showOptions = false
        }
    }

    fun onOptionDismiss() {
        if (selectedStatus != MangaStatus.None) showOptions = false
    }
}

