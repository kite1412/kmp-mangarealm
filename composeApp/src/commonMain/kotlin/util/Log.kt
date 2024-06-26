package util

import io.github.aakira.napier.Napier

// wrapper class for Napier logger
class Log private constructor() {
    companion object {
        private const val GENERAL_TAG = "MRealm"
        fun v(message: String, tag: String = GENERAL_TAG) {
            Napier.v(message, tag = tag)
        }

        fun d(message: String, tag: String = GENERAL_TAG) {
            Napier.d(message, tag = tag)
        }

        fun i(message: String, tag: String = GENERAL_TAG) {
            Napier.i(message, tag = tag)
        }

        fun w(message: String, tag: String = GENERAL_TAG) {
            Napier.w(message, tag = tag)
        }

        fun e(message: String, tag: String = GENERAL_TAG) {
            Napier.e(message, tag = tag)
        }
    }
}