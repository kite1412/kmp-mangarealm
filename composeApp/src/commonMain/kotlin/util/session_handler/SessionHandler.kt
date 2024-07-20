package util.session_handler

import api.mangadex.model.response.ListResponse
import api.mangadex.service.MangaDex
import model.session.Session

interface SessionHandler<T, ATTR> {
    val session: Session<T, ATTR>
    val mangaDex: MangaDex

    suspend fun updateSession(onFinish: (Boolean, Session<T, ATTR>?) -> Unit)

    suspend fun <ATTR> needUpdate(
        response: ListResponse<ATTR>,
        action: suspend (newOffset: Int, prevResponse: ListResponse<ATTR>) -> Unit
    ): Boolean {
        val offset = response.offset
        val total = response.total
        val limit = response.limit
        if (offset < total && limit < total) {
            action(offset + limit, response)
            return true
        }
        return false
    }
}