package api.mangadex.util

import api.mangadex.model.request.ImageQuality
import api.mangadex.model.response.Data
import api.mangadex.model.response.HomeUrl
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
            if (e.value is List<*>) {
                (e.value as List<*>).forEach {
                    final += "${e.key}=${it.toString().replace(" ", "%20")}"
                }
            } else {
                final += "${e.key}=${e.value.toString().replace(" ", "%20")}"
            }
            if ((i + 1) != queryParams.size) final += "&"
        }
        if (otherParams.isNotEmpty()) {
            final += "&${trimStart(otherParams)}"
        }
        return final
    }
    return ""
}

fun constructQuery(vararg queries: String): String {
    var s = "?"
    queries.forEachIndexed { i, q ->
        var temp = q
        temp = trimStart(temp)
        s += temp
        if (i < queries.size) {
            s += "&"
        }
    }
    return s
}

fun getCoverUrl(mangaId: String, filename: String): String {
    val url = "${ApiConstant.COVER_DISCOVERY_ENDPOINT}/$mangaId/$filename"
    return url
}

fun getCoverUrl(manga: Data<MangaAttributes>): String {
    val id = manga.id
    var filename = ""
    for (r in manga.relationships) {
        if (r.type == DataType.COVER_ART) {
            r.attributes?.fileName?.let {
                filename = it
            }
            break
        }
    }
    return getCoverUrl(id, filename)
}

fun getTitle(title: Map<String, String>): String {
    return title["en"] ?: title["ja"] ?: title["ja-ro"] ?: ""
}

fun getDesc(desc: Map<String, String>): String {
    return desc["en"] ?: desc["id"] ?: desc["ja"] ?: ""
}

fun getTags(data: Data<MangaAttributes>): String = getTags(data.attributes)

fun getTags(manga: MangaAttributes): String {
    var s = ""
    manga.tags.forEachIndexed { i, t ->
        s += "${t.attributes.name["en"]}"
        if (i < (manga.tags.size - 1)) {
            s += ", "
        }
    }
    return s
}

fun getTagList(data: Data<MangaAttributes>): List<String> {
    val tags = getTags(data)
    return if (tags.isNotEmpty()) tags.split(", ") else listOf()
}

fun getTagList(manga: MangaAttributes): List<String> {
    val tags = getTags(manga)
    return if (tags.isNotEmpty()) tags.split(", ") else listOf()
}

fun getTagList(tags: String): List<String> = if (tags.isNotEmpty()) tags.split(", ")
    else listOf()

fun getChapterImageUrl(
    baserUrl: String,
    hash: String,
    imageQuality: ImageQuality,
    filename: String
): String = "$baserUrl/$imageQuality/$hash/$filename"

fun getChapterImageUrls(
    url: HomeUrl,
    imageQuality: ImageQuality
): List<String> {
    val fileUrls = if (imageQuality == ImageQuality.DATA) url.chapter.data
        else url.chapter.dataSaver

    return mutableListOf<String>().run {
        fileUrls.forEach {
            add(getChapterImageUrl(
                baserUrl = url.baseUrl,
                hash = url.chapter.hash,
                imageQuality = imageQuality,
                filename = it
            ))
        }
        this
    }
}