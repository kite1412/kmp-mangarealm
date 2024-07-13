package util.session_handler

import Libs
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import model.Manga
import model.session.Session
import model.toMangaList
import util.retry

class MangaSessionHandler(
    override val session: Session<Manga, MangaAttributes>,
    override val mangaDex: MangaDex = Libs.mangaDex
): SessionHandler<Manga, MangaAttributes> {
    override suspend fun updateSession(onFinish: (Boolean, Session<Manga, MangaAttributes>?) -> Unit) {
        val offset = session.response.offset
        val total = session.response.total
        val limit = session.response.limit
        if (offset < total && limit < total) {
            session.queries["offset"] = offset + limit
            session.queries["limit"] = limit
            val res = retry<ListResponse<MangaAttributes>?>(
                count = 3,
                predicate = { it == null || it.errors != null}
            ) { mangaDex.getManga(generateQuery(session.queries)) }
            if (res != null) {
                session.addAll(res.toMangaList())
                session.newResponse(res)
                onFinish(res.data.isEmpty(), session)
                return
            }
            onFinish(true, null)
        }
        onFinish(true, null)
    }
}