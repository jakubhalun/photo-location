package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class LocationResultTextFormatter {
    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC)
    }

    fun prepareText(locationResult: LocationResult, timeWithDuration: Instant): String =
        """
            JPEG creation time (after applying zone change): <b>${instantInformation(timeWithDuration)}</b></br></br>
            Last point before:</br>
            ${formatPoint(locationResult.lastBefore)}
            Point closest in time:</br>
            ${formatPoint(locationResult.closestInTime)}
            Stops nearby (in time):</br></br>
            ${formatList(locationResult.stopPoints)}
        """.trimIndent()

    private fun instantInformation(timeWithDuration: Instant) = formatter.format(timeWithDuration)

    private fun formatList(stops: List<TravelPoint>) = stops.map { formatPoint(it) }

    private fun formatPoint(point: TravelPoint): String =
        """
            "Latitude: ${point.location.latitude}, Longitude: ${point.location.longitude}, time=${instantInformation(point.timeUtc)}</br>
            Commons template: <b><code>{{Location|${point.location.longitude}|${point.location.latitude}}}</code></b></br>
            Google Maps Link: <a href="https://maps.google.com/maps?ll=${point.location.longitude},${point.location.latitude}&spn=0.01,0.01&t=h&q=${point.location.longitude},${point.location.latitude}">link</a></br></br>
        """.trimIndent()
}
