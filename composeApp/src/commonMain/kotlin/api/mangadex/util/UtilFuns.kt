package api.mangadex.util

import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes

fun generateArrayQueryParam(name: String, values: List<Any>): String {
    if (values.isNotEmpty()) {
        var final = "?"
        values.forEachIndexed { i, v ->
            final += "$name=$v"
            if ((i + 1) != values.size) final += "&"
        }
        return final
    }
    return ""
}

private fun trimStart(params: String): String {
    return params.replace("?", "")
}

fun generateQuery(queryParams: Map<String, Any>, otherParams: String = ""): String {
    if (queryParams.isNotEmpty()) {
        var final = "?"
        queryParams.entries.forEachIndexed { i, e ->
            final += "${e.key}=${e.value}"
            if ((i + 1) != queryParams.size) final += "&"
        }
        if (otherParams.isNotEmpty()) {
            final += "&${trimStart(otherParams)}"
        }
        return final
    }
    return ""
}

fun getCoverUrl(mangaId: String, filename: String): String {
    return "${ApiConstant.COVER_DISCOVERY_ENDPOINT}/$mangaId/$filename"
}

fun getCoverUrl(manga: Data<MangaAttributes>): String {
    val id = manga.id
    var filename = ""
    for (r in manga.relationships) {
        if (r.type == DataType.COVER_ART) {
            filename = r.attributes!!.fileName!!
            break
        }
    }
    return getCoverUrl(id, filename)
}