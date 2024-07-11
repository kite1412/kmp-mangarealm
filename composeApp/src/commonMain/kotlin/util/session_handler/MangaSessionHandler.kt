package util.session_handler

import Libs
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import model.Manga
import model.session.Session
import model.toMangaMap
import util.retry

class MangaSessionHandler(
    override val session: Session<String, Manga, MangaAttributes>,
    override val mangaDex: MangaDex = Libs.mangaDex
): SessionHandler<String, Manga, MangaAttributes> {
    override suspend fun updateSession(finish: (Boolean) -> Unit) {
        val offset = session.response.offset
        val total = session.response.total
        if (offset < total || session.url.isNotEmpty()) {
            val limit = session.response.limit
            session.queries["offset"] = offset + limit
            session.queries["limit"] = limit
            val res = retry<ListResponse<MangaAttributes>?>(
                count = 3,
                predicate = { it == null || it.errors != null}
            ) { mangaDex.getManga(generateQuery(session.queries)) }
            if (res != null) {
                session.putAll(res.toMangaMap())
                session.newResponse(res)
                finish(false)
                return
            }
            finish(true)
        }
        finish(true)
    }
}