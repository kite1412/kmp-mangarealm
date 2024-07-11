package model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.ApiConstant

interface Session<K, T, ATTR> {
    var url: String
    var response: ListResponse<ATTR>
    var data: SnapshotStateMap<K, T>

    fun put(key: K, value: T) { data[key] = value }

    fun putAll(map: Map<K, T>) = data.putAll(map)

    fun clear() {
        response = ListResponse()
        data.clear()
    }

    fun from(session: Session<K, T, ATTR>) {
        response = session.response
        data.putAll(session.data)
    }

    fun addQueries(queries: String) { url += queries }

    fun newResponse(res: ListResponse<ATTR>) { response = res }
}

data class MangaSession(
    override var url: String = ApiConstant.MANGA_ENDPOINT,
    override var response: ListResponse<MangaAttributes> = ListResponse(),
    override var data: SnapshotStateMap<String, Manga> = mutableStateMapOf()
): Session<String, Manga, MangaAttributes>