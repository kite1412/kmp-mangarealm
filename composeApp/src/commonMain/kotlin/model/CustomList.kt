package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.CustomListAttributes

data class CustomList(
    var data: MutableState<Data<CustomListAttributes>>,
    val mangaIds: SnapshotStateList<String>,
    val manga: SnapshotStateList<Manga>,
    val deleted: Boolean
)

fun Data<CustomListAttributes>.toCustomList(): CustomList
    = CustomList(mutableStateOf(this), mutableStateListOf(), mutableStateListOf(), false)

fun Data<CustomListAttributes>.getMangaIds(): List<String> =
    relationships.filter { it.type == "manga" }
        .map { it.id }