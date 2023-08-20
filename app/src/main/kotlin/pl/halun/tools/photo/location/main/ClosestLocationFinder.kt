package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Duration
import java.time.Instant

class ClosestLocationFinder {
    fun findClosest(track: List<TravelPoint>, time: Instant): LocationResult =
        LocationResult(
            findLastBefore(track, time),
            findClosestInTime(track, time),
            findAllStopsCloseInTime(track, time)
        )

    private fun findLastBefore(track: List<TravelPoint>, time: Instant): TravelPoint =
        track.filter { travelPoint -> travelPoint.timeUtc.isBefore(time) }.maxBy { it.timeUtc }

    private fun findClosestInTime(track: List<TravelPoint>, time: Instant): TravelPoint {
        return track.minBy { travelPoint ->
            Duration.between(travelPoint.timeUtc, time).abs().seconds
        }
    }

    private fun findAllStopsCloseInTime(track: List<TravelPoint>, time: Instant): List<TravelPoint> {
        val twoMinutes = Duration.ofMinutes(2)
        val tenSeconds = Duration.ofSeconds(10)

        val relevantTrack = track.filter { travelPoint ->
            Duration.between(travelPoint.timeUtc, time).abs() <= twoMinutes
        }

        val stops = mutableListOf<TravelPoint>()
        for (i in 0 until relevantTrack.size - 1) {
            val diff = Duration.between(relevantTrack[i].timeUtc, relevantTrack[i + 1].timeUtc)
            if (diff > tenSeconds) {
                stops.add(relevantTrack[i])
            }
        }
        return stops
    }
}
