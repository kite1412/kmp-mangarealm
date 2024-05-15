package api.mangadex.service

import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.Token

interface MangaDex {

    suspend fun getToken(request: TokenRequest): Token?
}