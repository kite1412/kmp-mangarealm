package api.mangadex.service

import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.ClientSecret
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.Token
import api.mangadex.model.response.attribute.ClientAttributes

interface MangaDex {
    suspend fun login(request: TokenRequest): Token?

    suspend fun getClientSecret(id: String): ClientSecret?

    suspend fun getClients(): ListResponse<ClientAttributes, Unit>?
}