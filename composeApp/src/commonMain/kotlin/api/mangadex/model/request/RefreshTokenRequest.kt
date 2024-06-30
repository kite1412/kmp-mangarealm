package api.mangadex.model.request

import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.getOrNull
import util.KottageConst
import util.Log

data class RefreshTokenRequest(
    val grantType: String = "refresh_token",
    val refreshToken: String,
    val reserveRefreshToken: String,
    val clientId: String,
    val clientSecret: String
)

suspend fun RefreshTokenRequest(storage: KottageStorage): RefreshTokenRequest {
    val refToken = storage.get<String>(KottageConst.REFRESH_TOKEN)
    // null on first launch
    val resRefToken = storage.getOrNull<String>(KottageConst.RESERVE_REFRESH_TOKEN)
    val clientId = storage.get<String>(KottageConst.CLIENT_ID)
    val clientSecret = storage.get<String>(KottageConst.CLIENT_SECRET)
    return RefreshTokenRequest(
        grantType = "refresh_token",
        refreshToken = refToken,
        reserveRefreshToken = resRefToken ?: refToken,
        clientId = clientId,
        clientSecret = clientSecret
    )
}