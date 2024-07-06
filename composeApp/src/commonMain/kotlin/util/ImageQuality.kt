package util

import api.mangadex.model.request.ImageQuality as quality

object ImageQuality {
    const val HIGH = "High"
    const val DATA_SAVER = "Data Saver"

    // maps to api's image quality representation
    operator fun invoke(imageQuality: String): quality = when(imageQuality) {
        DATA_SAVER -> quality.DATA_SAVER
        else -> quality.DATA
    }
}