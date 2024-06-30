package api.mangadex.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("refresh_expires_in")
    val refreshExpiresIn: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("not-before-policy")
    val notBeforePolicy: Int,
    @SerialName("session_state")
    val sessionState: String,
    val scope: String,
    @SerialName("client_type")
    val clientType: String,
    val error: String? = null,
    @SerialName("error_description")
    val errorDescription: String? = null
)
