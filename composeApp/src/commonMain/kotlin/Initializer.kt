import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.TagAttributes
import api.mangadex.service.MangaDex
import io.github.irgaly.kottage.KottageList
import io.github.irgaly.kottage.KottageListValue
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.kottageListValue
import io.github.irgaly.kottage.put
import model.Tag
import util.ADVENTURE_TAG
import util.COMEDY_TAG
import util.KottageConst
import util.Log
import util.MYSTERY_TAG
import util.PSYCHOLOGICAL_TAG
import util.ROMANCE_TAG

class Initializer(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val kottage: KottageStorage = Libs.kottageStorage
) {
    private fun mapTags(raw: List<Data<TagAttributes>>): List<KottageListValue<Tag>> {
        return raw.map { d ->
            kottageListValue(key = d.id, value = Tag(d.id, d.attributes.name["en"]!!))
        }
    }

    /** return id of given tag's [name] */
    private suspend fun filterTags(
        name: String,
        kl: KottageList
    ): String? {
        if (kl.isNotEmpty()) {
            for (i in 0 until kl.getSize()) {
                val now = kl.getByIndex(i)!!.value<Tag>()
                if (now.name == name) {
                    return now.tagId
                }
            }
        }
        return null
    }

    private suspend fun setupTags(postSetup: suspend (mandatoryTags: Map<String, String>) -> Unit) {
        val tags = kottage.list(KottageConst.TAGS_LIST)
        if (tags.isEmpty()) {
            val res = mangaDex.getTags()
            if (res != null) {
                tags.addAll(mapTags(res.data))
            } else Log.w("Fail to fetch tags")
        }
        tags.getFirst()?.let {
            Log.d("(setupTags) first item: ${it.positionId}:{${it.value<Tag>().tagId}:${it.value<Tag>().name}}")
        }
        val mandatoryTags = mutableMapOf<String, String>()
        if (!kottage.exists(KottageConst.ROMANCE_TAG_ID)) {
            val res = filterTags(name = ROMANCE_TAG, kl = tags)
            if (res != null) kottage.put(KottageConst.ROMANCE_TAG_ID, res)
        }
        if (!kottage.exists(KottageConst.COMEDY_TAG_ID)) {
            val res = filterTags(name = COMEDY_TAG, kl = tags)
            if (res != null) kottage.put(KottageConst.COMEDY_TAG_ID, res)
        }
        if (!kottage.exists(KottageConst.ADVENTURE_TAG_ID)) {
            val res = filterTags(name = ADVENTURE_TAG, kl = tags)
            if (res != null) kottage.put(KottageConst.ADVENTURE_TAG_ID, res)
        }
        if (!kottage.exists(KottageConst.PSYCHOLOGICAL_TAG_ID)) {
            val res = filterTags(name = PSYCHOLOGICAL_TAG, kl = tags)
            if (res != null) kottage.put(KottageConst.PSYCHOLOGICAL_TAG_ID, res)
        }
        if (!kottage.exists(KottageConst.MYSTERY_TAG_ID)) {
            val res = filterTags(name = MYSTERY_TAG, kl = tags)
            if (res != null) kottage.put(KottageConst.MYSTERY_TAG_ID, res)
        }
        val romId = kottage.get<String>(KottageConst.ROMANCE_TAG_ID)
        val comId = kottage.get<String>(KottageConst.COMEDY_TAG_ID)
        val advId = kottage.get<String>(KottageConst.ADVENTURE_TAG_ID)
        val psyId = kottage.get<String>(KottageConst.PSYCHOLOGICAL_TAG_ID)
        val mysId = kottage.get<String>(KottageConst.MYSTERY_TAG_ID)
        mandatoryTags.putAll(mapOf(
            ROMANCE_TAG to romId,
            COMEDY_TAG to comId,
            ADVENTURE_TAG to advId,
            PSYCHOLOGICAL_TAG to psyId,
            MYSTERY_TAG to mysId,
        ))
        postSetup(mandatoryTags)
    }

    suspend operator fun invoke(postTagSetup: suspend (mandatoryTags: Map<String, String>) -> Unit) {
        setupTags(postSetup = postTagSetup)
    }
}