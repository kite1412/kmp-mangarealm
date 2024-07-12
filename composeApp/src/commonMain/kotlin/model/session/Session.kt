package model.session

import androidx.compose.runtime.snapshots.SnapshotStateList
import api.mangadex.model.response.ListResponse

interface Session<T, ATTR> {
    val url: String
    val queries: MutableMap<String, Any>
    var response: ListResponse<ATTR>
    var data: SnapshotStateList<T>

    fun addAll(list: List<T>) = data.addAll(list)

    fun clear() {
        response = ListResponse()
        data.clear()
    }

    fun from(session: Session<T, ATTR>) {
        response = session.response
        data.addAll(session.data)
    }

    fun newResponse(res: ListResponse<ATTR>) { response = res }

    fun putAllQueries(queries: Map<String, Any>) = this.queries.putAll(queries)
}