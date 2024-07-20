package model

import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.CustomListAttributes

data class CustomList(
    val data: Data<CustomListAttributes>,
    val deleted: Boolean
)

fun Data<CustomListAttributes>.toCustomList(): CustomList
    = CustomList(this, false)
