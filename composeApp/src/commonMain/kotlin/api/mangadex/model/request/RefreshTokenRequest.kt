package api.mangadex.model.request

import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import util.KottageConst

data class RefreshTokenRequest(
    val grantType: String = "refresh_token",
    val refreshToken: String,
    val clientId: String,
    val clientSecret: String
)

suspend fun RefreshTokenRequest(storage: KottageStorage): RefreshTokenRequest {
    val refToken = storage.get<String>(KottageConst.REFRESH_TOKEN)
    val clientId = storage.get<String>(KottageConst.CLIENT_ID)
    val clientSecret = storage.get<String>(KottageConst.CLIENT_SECRET)
    return RefreshTokenRequest(
        grantType = "refresh_token",
        refreshToken = refToken,
        clientId = clientId,
        clientSecret = clientSecret
    )
}