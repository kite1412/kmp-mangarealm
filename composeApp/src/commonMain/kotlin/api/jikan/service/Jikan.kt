package api.jikan.service

import api.jikan.model.Character
import api.jikan.model.Data

interface Jikan {
    suspend fun getMangaCharacters(mangaMalId: Int): Data<List<Character>>?
}