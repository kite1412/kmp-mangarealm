package model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.CustomListAttributes

data class CustomList(
    val data: Data<CustomListAttributes>,
    val mangaIds: MutableList<String>,
    val manga: SnapshotStateList<String>,
    val deleted: Boolean
)

fun Data<CustomListAttributes>.toCustomList(): CustomList
    = CustomList(this, mutableListOf(), mutableStateListOf(), false)

fun Data<CustomListAttributes>.getMangaIds(): List<String> =
    relationships.filter { it.type == "manga" }
        .map { it.id }