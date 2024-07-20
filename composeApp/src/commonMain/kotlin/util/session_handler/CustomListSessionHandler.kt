package util.session_handler

import Libs
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.service.MangaDex
import model.CustomList
import model.session.Session

class CustomListSessionHandler(
    override val session: Session<CustomList, CustomListAttributes>,
    override val mangaDex: MangaDex = Libs.mangaDex
) : SessionHandler<CustomList, CustomListAttributes> {
    override suspend fun updateSession(onFinish: (Boolean, Session<CustomList, CustomListAttributes>?) -> Unit) {
        val needUpdate = needUpdate(session.response) { newOffset, prevResponse ->
            // TODO implement
        }
        if (!needUpdate) onFinish(true, null)
    }
}