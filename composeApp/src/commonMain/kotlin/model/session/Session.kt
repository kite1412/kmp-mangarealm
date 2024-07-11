package model.session

import androidx.compose.runtime.snapshots.SnapshotStateMap
import api.mangadex.model.response.ListResponse

interface Session<K, T, ATTR> {
    val url: String
    val queries: MutableMap<String, Any>
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

    fun newResponse(res: ListResponse<ATTR>) { response = res }

    fun putAllQueries(queries: Map<String, Any>) = this.queries.putAll(queries)
}