package model

import api.mangadex.util.Status as s

data class Status(
    val rawStatus: String,
    val status: String,
)

object MangaStatus {
    val None = Status("", "Reading")
    val All = Status("", "All")
    val Reading = Status(s.READING, "Reading")
    val OnHold = Status(s.ON_HOLD, "On Hold")
    val PlanToRead = Status(s.PLAN_TO_READ, "Plan to Read")
    val Completed = Status(s.COMPLETED, "Completed")
    val Dropped = Status(s.DROPPED, "Dropped")
    val ReReading = Status(s.REREADING, "Re-Reading")

    operator fun invoke(includeAll: Boolean = false): List<Status> {
        val l = listOf(
            Reading,
            OnHold,
            PlanToRead,
            Completed,
            Dropped,
            ReReading
        )
        return if (!includeAll) l else listOf(All) + l
    }
}