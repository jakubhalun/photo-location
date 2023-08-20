package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class LocationInTimeTextProvider(
    private val closestLocationFinder: ClosestLocationFinder,
    private val textFormatter: LocationResultTextFormatter
) {

    private var travelPoints: List<TravelPoint> = emptyList()
    private var time: Instant? = null
    private var differenceToUtc: Duration = Duration.ZERO
    private var timeWithDuration: Instant? = null

    fun numberOfTravelPoints(): Int = travelPoints.size

    fun loadedTime(): String = time?.toString() ?: throw IllegalStateException("Timestamp not set yet")

    fun textForChangedTime(time: Instant): String {
        this.time = time
        timeWithDuration = time.plus(differenceToUtc)
        return generateText()
    }

    fun textForChangedLocations(travelPoints: List<TravelPoint>): String {
        this.travelPoints = travelPoints
        return generateText()
    }

    fun textForChangedDifferenceToUtc(difference: String): String {
        val duration = differenceUtcForOffset(difference)
        this.differenceToUtc = duration
        time?.let { timeWithDuration = time!!.plus(duration) }
        return generateText()
    }

    private fun differenceUtcForOffset(offsetString: String): Duration {
        val parts = offsetString.split(":")

        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()

        return Duration.ofHours(hours.toLong()).plus(Duration.ofMinutes(minutes.toLong()))
    }

    private fun generateText(): String =
        if (readyToGenerateText()) {
            if (longMismatchBetweenJpegAndKml()) {
                "The JPEG creation time is well outside of tracked travel points times (wrong selection of KML file?). ${trackedTime()}"
            } else if (shortMismatchBetweenJpegAndKml()) {
                "The JPEG creation time is outside of tracked travel points times (wrong selection of time zone?). ${trackedTime()}"
            } else {
                val result = closestLocationFinder.findClosest(travelPoints, timeWithDuration!!)
                textFormatter.prepareText(result, timeWithDuration!!)
            }
        } else {
            "Missing part of information to generate report"
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
