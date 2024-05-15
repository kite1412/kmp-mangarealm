package util

import io.github.aakira.napier.Napier

// wrapper class for Napier logger
class Log private constructor() {
    companion object {
        fun v(message: String) {
            Napier.v(message)
        }

        fun d(message: String) {
            Napier.d(message)
        }

        fun i(message: String) {
            Napier.i(message)
        }

        fun w(message: String) {
            Napier.w(message)
        }

        fun e(message: String) {
            Napier.e(message)
        }
    }
}