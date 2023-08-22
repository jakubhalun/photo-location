package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class LocationInTimeProvider(
    private val closestLocationFinder: ClosestLocationFinder
) {
    private var travelPoints: List<TravelPoint> = emptyList()
    private var time: Instant? = null
    private var differenceToUtc: Duration = Duration.ZERO
    private var timeWithDuration: Instant? = null

    fun numberOfTravelPoints(): Int = travelPoints.size

    fun loadedTime(): String = time?.toString() ?: throw IllegalStateException("Timestamp not set yet")

    fun resultForChangedTime(time: Instant): Result {
        this.time = time
        timeWithDuration = time.plus(differenceToUtc)
        return prepareResult()
    }

    fun resultForChangedLocations(travelPoints: List<TravelPoint>): Result {
        this.travelPoints = travelPoints
        return prepareResult()
    }

    fun resultForChangedDifferenceToUtc(difference: String): Result {
        val duration = differenceUtcForOffset(difference)
        this.differenceToUtc = duration
        time?.let { timeWithDuration = time!!.plus(duration) }
        return prepareResult()
    }

    private fun differenceUtcForOffset(offsetString: String): Duration {
        val parts = offsetString.split(":")

        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()

        return Duration.ofHours(hours.toLong()).plus(Duration.ofMinutes(minutes.toLong()))
    }

    private fun prepareResult(): Result =
        if (readyToGenerateText()) {
            if (longMismatchBetweenJpegAndKml()) {
                InvalidResult(
                    "The JPEG creation time is well outside of tracked travel points (wrong selection of KML file?). ${trackedTime()}"
                )
            } else if (shortMismatchBetweenJpegAndKml()) {
                InvalidResult(
                    "The JPEG creation time is outside of tracked travel points (wrong selection of time zone?). ${trackedTime()}"
                )
            } else {
                closestLocationFinder.findClosest(travelPoints, timeWithDuration!!)
            }
        } else {
            InvalidResult("Missing part of information to generate report")
        }

    private fun readyToGenerateText() = travelPoints.isNotEmpty() && timeWithDuration != null

    private fun longMismatchBetweenJpegAndKml(): Boolean {
        val startOfTrack = travelPoints.first()
        val endOfTrack = travelPoints.last()
        return timeWithDuration!!.isBefore(startOfTrack.timeUtc.minus(1, ChronoUnit.DAYS))
                || timeWithDuration!!.isAfter(endOfTrack.timeUtc.plus(1, ChronoUnit.DAYS))
    }

    private fun shortMismatchBetweenJpegAndKml(): Boolean {
        val startOfTrack = travelPoints.first()
        val endOfTrack = travelPoints.last()
        return timeWithDuration!!.isBefore(startOfTrack.timeUtc.minus(1, ChronoUnit.MINUTES))
                || timeWithDuration!!.isAfter(endOfTrack.timeUtc.plus(15, ChronoUnit.MINUTES))
    }

    private fun trackedTime(): String = "Tracked time between ${travelPoints.first().timeUtc} and ${travelPoints.last().timeUtc}"
}
