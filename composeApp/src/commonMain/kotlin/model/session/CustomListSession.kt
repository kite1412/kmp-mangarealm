package model.session

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.util.ApiConstant
import model.CustomList

data class CustomListSession(
    override val url: String = ApiConstant.USER_CUSTOM_LIST,
    override val queries: MutableMap<String, Any> = mutableMapOf("limit" to 100),
    override var response: ListResponse<CustomListAttributes> = ListResponse(),
    override var data: SnapshotStateList<CustomList> = mutableStateListOf(),
    override var state: MutableState<SessionState> = mutableStateOf(SessionState.IDLE)
) : Session<CustomList, CustomListAttributes>