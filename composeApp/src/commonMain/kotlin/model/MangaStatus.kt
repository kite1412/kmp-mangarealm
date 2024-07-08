package model

data class Status(
    val rawStatus: String,
    val status: String,
)

object MangaStatus {
    val Reading = Status("reading", "Reading")
    val OnHold = Status("on_hold", "On Hold")
    val PlanToRead = Status("plan_to_read", "Plan to Read")
    val Completed = Status("completed", "Completed")
    val Dropped = Status("dropped", "Dropped")
    val ReReading = Status("re_reading", "Re-Reading")

    operator fun invoke(): List<Status> = listOf(
        Reading,
        OnHold,
        PlanToRead,
        Completed,
        Dropped,
        ReReading
    )
}