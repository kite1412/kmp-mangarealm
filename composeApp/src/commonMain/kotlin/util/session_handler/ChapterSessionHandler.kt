package util.session_handler

import Libs
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import model.Chapter
import model.session.Session
import model.toChapters
import util.retry

class ChapterSessionHandler(
    override val session: Session<Chapter, ChapterAttributes>,
    override val mangaDex: MangaDex = Libs.mangaDex
) : SessionHandler<Chapter, ChapterAttributes> {
    override suspend fun updateSession(onFinish: (Boolean, Session<Chapter, ChapterAttributes>?) -> Unit) {
        val needUpdate = needUpdate(session.response) { newOffset, prevResponse ->
            session.queries["offset"] = newOffset
            session.queries["limit"] = prevResponse.limit
            retry<ListResponse<ChapterAttributes>?>(
                count = 3,
                predicate = { it == null || it.errors != null }
            ) {
                mangaDex.paging.chapters(prevResponse, generateQuery(session.queries))
            }?.let {
                session.setActive(it, it.toChapters())
                onFinish(it.data.isEmpty(), session)
                return@needUpdate
            }
            onFinish(true, null)
        }
        if (!needUpdate) onFinish(true, null)
    }
}