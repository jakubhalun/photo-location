package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Duration
import java.time.Instant

class LocationInTimeTextProvider {

    private var travelPoints: List<TravelPoint> = emptyList()
    private var time: Instant? = null
    private var differenceToUtc: Duration = Duration.ZERO
    private var timeWithDuration: Instant? = null

    fun textForChangedTime(time: Instant): String {
        this.time = time
        return time.toString()
    }

    fun textForChangedLocations(travelPoints: List<TravelPoint>): String {
        this.travelPoints = travelPoints
        return travelPoints.first().toString()
    }

    fun textForChangedDifferenceToUtc(difference: String): String {
        val duration = differenceUtcForOffset(difference)
        this.differenceToUtc = duration
        time?.let { timeWithDuration = time!!.plus(duration) }
        // TODO
        return timeWithDuration ?.let { timeWithDuration!!.toString() } ?: duration.toString()
    }

    private fun differenceUtcForOffset(offsetString: String): Duration {
        val parts = offsetString.split(":")

        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()

        return Duration.ofHours(hours.toLong()).plus(Duration.ofMinutes(minutes.toLong()))
    }
}
