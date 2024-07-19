package view_model.main.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.MangaStatus
import model.Status
import view_model.SharedViewModel
import view_model.main.MainViewModel

class UserListState(
    val vm: MainViewModel,
    val sharedViewModel: SharedViewModel = vm.sharedViewModel,
    private val scope: CoroutineScope = vm.viewModelScope
) {
    var selectedStatus by mutableStateOf(MangaStatus.None)
    var showOptions by mutableStateOf(true)
    val sizeRatio by derivedStateOf { if (showOptions) 0.5f else 1f }
    val manga by derivedStateOf { sharedViewModel.mangaStatus[selectedStatus] ?: emptyList() }

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

    fun showOptions() { showOptions = true }
}

