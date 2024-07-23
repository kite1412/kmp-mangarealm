package util.session_handler

import Libs
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.service.MangaDex
import model.Chapter
import model.session.Session

class ChapterSessionHandler(
    override val session: Session<Chapter, ChapterAttributes>,
    override val mangaDex: MangaDex = Libs.mangaDex
) : SessionHandler<Chapter, ChapterAttributes> {
    override suspend fun updateSession(onFinish: (Boolean, Session<Chapter, ChapterAttributes>?) -> Unit) {
        onFinish(true, null)
    }
}