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
        queries.clear()
    }

    fun from(session: Session<T, ATTR>) {
        newResponse(session.response)
        addAll(session.data.subList(data.size, session.data.size))
        putAllQueries(session.queries)
    }

    fun clearFrom(session: Session<T, ATTR>) {
        clear()
        from(session)
    }

    fun newResponse(res: ListResponse<ATTR>) { response = res }

    fun putAllQueries(queries: Map<String, Any>) = this.queries.putAll(queries)
}

fun <T, ATTR> Session<T, ATTR>.isEmpty() =
    response == ListResponse<ATTR>() && queries.isEmpty() && data.isEmpty()

fun <T, ATTR> Session<T, ATTR>.isNotEmpty() = !isEmpty()