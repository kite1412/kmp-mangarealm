package view_model.main.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.MangaStatus
import model.Status

class UserListState {
    var selectedStatus by mutableStateOf(MangaStatus.None)
    var showOptions by mutableStateOf(false)

    fun onStatusSelected(new: Status) {
        selectedStatus = new
    }
}

