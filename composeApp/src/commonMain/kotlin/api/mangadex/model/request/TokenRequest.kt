package api.mangadex.model.request

import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import util.KottageConst as c

data class TokenRequest(
    val grantType: String = "password",
    val username: String,
    val password: String,
    val clientId: String,
    val clientSecret: String,
) {
    companion object {
        suspend fun fromLocal(storage: KottageStorage): TokenRequest = TokenRequest(
            username = storage.get<String>(c.USERNAME),
            password = storage.get<String>(c.PASSWORD),
            clientId = storage.get<String>(c.CLIENT_ID),
            clientSecret = storage.get<String>(c.CLIENT_SECRET)
        )
    }
}