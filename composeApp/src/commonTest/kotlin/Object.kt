import api.mangadex.service.MangaDexImpl
import api.mangadex.service.TokenHandler

val api = MangaDexImpl(token = object : TokenHandler {
    override suspend fun invoke(): String {
        return ""
    }
})