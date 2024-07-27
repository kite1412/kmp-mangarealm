package api.jikan.service

import api.jikan.model.Character
import api.jikan.model.Data
import api.jikan.util.ApiConstant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import util.Log

class JikanImpl(
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
) : Jikan {
    private suspend inline fun <reified R> get(
        url: String,
        methodName: String
    ): R? = try {
        client.get(url).body<R?>()
    } catch (e: Exception) {
        e.message?.let {
            Log.e("($methodName) $it")
        }
        null
    }

    override suspend fun getMangaCharacters(mangaMalId: Int): Data<List<Character>>? =
        get<Data<List<Character>>?>(
            url = ApiConstant.mangaCharacters(mangaMalId),
            methodName = "getMangaCharacters"
        )?.also {
            Log.d("GET (jikan:getMangaCharacters) data length: ${it.data.size}")
        }
}