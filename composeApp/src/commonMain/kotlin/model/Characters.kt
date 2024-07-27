package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import api.jikan.model.Character

data class Characters(
    val data: SnapshotStateList<Character> = mutableStateListOf(),
    var fetched: MutableState<Boolean> = mutableStateOf(false)
)
