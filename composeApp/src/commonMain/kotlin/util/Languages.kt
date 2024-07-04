package util

import model.Language

val languages = listOf(
    Language("en", "English"),
    Language("id", "Indonesian"),
    Language("fr", "French"),
    Language("es", "Spanish"),
    Language("de", "German"),
    Language("it", "Italian"),
    Language("pt", "Portuguese"),
    Language("nl", "Dutch"),
    Language("ru", "Russian"),
    Language("zh", "Chinese (Simplified)"),
    Language("ja", "Japanese"),
    Language("ko", "Korean"),
    Language("ar", "Arabic"),
    Language("hi", "Hindi"),
    Language("bn", "Bengali"),
    Language("ur", "Urdu"),
    Language("tr", "Turkish"),
    Language("fa", "Persian"),
    Language("el", "Greek"),
    Language("he", "Hebrew"),
    Language("th", "Thai"),
    Language("vi", "Vietnamese"),
    Language("ms", "Malay"),
    Language("sv", "Swedish"),
    Language("da", "Danish"),
    Language("no", "Norwegian"),
    Language("fi", "Finnish"),
    Language("cs", "Czech"),
    Language("pl", "Polish"),
    Language("hu", "Hungarian"),
    Language("ro", "Romanian"),
    Language("bg", "Bulgarian"),
    Language("hr", "Croatian"),
    Language("sr", "Serbian"),
    Language("sk", "Slovak"),
    Language("sl", "Slovenian"),
    Language("uk", "Ukrainian"),
    Language("lt", "Lithuanian"),
    Language("lv", "Latvian"),
    Language("et", "Estonian"),
    Language("fil", "Filipino"),
    Language("sw", "Swahili"),
    Language("af", "Afrikaans"),
    Language("is", "Icelandic"),
    Language("ga", "Irish"),
    Language("cy", "Welsh"),
    Language("eu", "Basque"),
    Language("ca", "Catalan"),
    Language("gl", "Galician"),
    Language("mt", "Maltese"),
    Language("sq", "Albanian"),
    Language("hy", "Armenian"),
    Language("az", "Azerbaijani"),
    Language("be", "Belarusian"),
    Language("ka", "Georgian"),
    Language("kk", "Kazakh"),
    Language("mk", "Macedonian"),
    Language("mn", "Mongolian"),
    Language("ps", "Pashto"),
    Language("si", "Sinhala"),
    Language("tg", "Tajik"),
    Language("ta", "Tamil"),
    Language("te", "Telugu"),
    Language("uz", "Uzbek"),
    Language("am", "Amharic"),
    Language("yo", "Yoruba"),
    Language("ha", "Hausa"),
    Language("zu", "Zulu"),
    Language("st", "Sesotho"),
    Language("xh", "Xhosa"),
    Language("pt-br", "Brazilian Portuguese"),
    Language("zh-hk", "Chinese"),
    Language("es-la", "Latin American Spanish")
)

fun mapLanguage(code: String): String {
    languages.forEach {
        if (it.code == code) return it.language
    }
    return code
}

fun mapCode(language: String): String {
    languages.forEach {
        if (it.language == language) return it.code
    }
    return language
}