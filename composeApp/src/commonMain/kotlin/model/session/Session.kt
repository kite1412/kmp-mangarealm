package model.session

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import api.mangadex.model.response.ListResponse

interface Session<T, ATTR> {
    val url: String
    val queries: MutableMap<String, Any>
    var response: ListResponse<ATTR>
    var data: SnapshotStateList<T>
    var state: MutableState<SessionState>

    fun addAll(list: List<T>) = data.addAll(list)

    fun clear() {
        response = ListResponse()
        data.clear()
        queries.clear()
        state.value = SessionState.IDLE
    }

    fun from(session: Session<T, ATTR>): Session<T, ATTR> {
        newResponse(session.response)
        addAll(session.data.subList(data.size, session.data.size))
        putAllQueries(session.queries)
        setState(session.state.value)
        return this
    }

    fun clearFrom(session: Session<T, ATTR>): Session<T, ATTR> {
        clear()
        return from(session)
    }

    fun newResponse(res: ListResponse<ATTR>) { response = res }

    fun putAllQueries(queries: Map<String, Any>) = this.queries.putAll(queries)

    fun setState(state: SessionState) { this.state.value = state }

    fun init(queries: Map<String, Any>) {
        setState(SessionState.FETCHING)
        putAllQueries(queries)
    }

    fun new(newResponse: ListResponse<ATTR>, data: List<T>) {
        newResponse(newResponse)
        addAll(data)
    }

    fun setActive(response: ListResponse<ATTR>, data: List<T>) {
        newResponse(response)
        addAll(data)
        setState(SessionState.ACTIVE)
    }
}

fun <T, ATTR> Session<T, ATTR>.isEmpty() =
    response == ListResponse<ATTR>() && data.isEmpty()

fun <T, ATTR> Session<T, ATTR>.isNotEmpty() = !isEmpty()

enum class SessionState {
    IDLE,
    FETCHING,
    ACTIVE
}