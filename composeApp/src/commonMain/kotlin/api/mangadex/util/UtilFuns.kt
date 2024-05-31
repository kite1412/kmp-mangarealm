package api.mangadex.util

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

fun coverImageUrl(mangaId: String, filename: String): String {
    return "${COVER_DISCOVERY_ENDPOINT}/$mangaId/$filename"
}