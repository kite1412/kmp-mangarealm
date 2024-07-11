package util.session_handler

import api.mangadex.service.MangaDex
import model.session.Session

interface SessionHandler<K, T, ATTR> {
    val session: Session<K, T, ATTR>
    val mangaDex: MangaDex

    suspend fun updateSession(finish: (Boolean) -> Unit)
}