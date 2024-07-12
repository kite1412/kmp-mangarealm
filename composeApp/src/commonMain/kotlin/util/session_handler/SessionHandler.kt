package util.session_handler

import api.mangadex.service.MangaDex
import model.session.Session

interface SessionHandler<T, ATTR> {
    val session: Session<T, ATTR>
    val mangaDex: MangaDex

    suspend fun updateSession(onFinish: (Boolean, Session<T, ATTR>?) -> Unit)
}