package util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Kottage's keys
object KottageConst {
    const val TOKEN = "token:accessToken"
    const val USERNAME = "profile:username"
    const val REFRESH_TOKEN = "token:refreshToken"
    const val RESERVE_REFRESH_TOKEN = "token:resRefreshToken"
    const val CLIENT_ID = "api:clientId"
    const val CLIENT_SECRET = "api:clientSecret"
    const val ROMANCE_TAG_ID = "tags:rom"
    const val COMEDY_TAG_ID = "tags:com"
    const val ADVENTURE_TAG_ID = "tags:adv"
    const val PSYCHOLOGICAL_TAG_ID = "tags:psy"
    const val MYSTERY_TAG_ID = "tags:mys"
    const val TAGS_LIST = "api:tags"
    const val HISTORY_LIST = "local:history"
}

const val SPLASH_TIME = 2000
const val LATEST_UPDATE_SLIDE_TIME = 3000
const val WARNING_TIME = 2000L
const val ROMANCE_TAG = "Romance"
const val COMEDY_TAG = "Comedy"
const val ADVENTURE_TAG = "Adventure"
const val PSYCHOLOGICAL_TAG = "Psychological"
const val MYSTERY_TAG = "Mystery"
const val ASCENDING = "asc"
const val DESCENDING = "desc"
const val DEFAULT_COLLECTION_SIZE = 20

val BLUR_TINT = Color.LightGray.copy(alpha = 0.4f)

val APP_BAR_HEIGHT = 62.dp