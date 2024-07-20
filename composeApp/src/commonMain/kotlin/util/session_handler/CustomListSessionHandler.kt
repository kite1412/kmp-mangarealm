package util.session_handler

import Libs
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.service.MangaDex
import model.session.Session

class CustomListSessionHandler(
    override val session: Session<Data<CustomListAttributes>, CustomListAttributes>,
    override val mangaDex: MangaDex = Libs.mangaDex
) : SessionHandler<Data<CustomListAttributes>, CustomListAttributes> {
    override suspend fun updateSession(onFinish: (Boolean, Session<Data<CustomListAttributes>, CustomListAttributes>?) -> Unit) {
        val needUpdate = needUpdate(session.response) { newOffset, prevResponse ->
            // TODO implement
        }
        if (!needUpdate) onFinish(true, null)
    }
}