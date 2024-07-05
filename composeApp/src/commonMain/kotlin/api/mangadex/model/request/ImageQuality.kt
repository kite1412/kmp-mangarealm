package api.mangadex.model.request

enum class ImageQuality {
    DATA, DATA_SAVER;

    override fun toString(): String = when(this) {
        DATA -> "data"
        DATA_SAVER -> "dataSaver"
    }
}