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

    fun prepareText(locationResult: LocationResult, timeWithDuration: Instant): String = """
JPEG creation time (after applying zone change): ${instantInformation(timeWithDuration)}

Last point before:
${formatPoint(locationResult.lastBefore)}

Point closest in time:
${formatPoint(locationResult.closestInTime)}

Stops nearby (in time):

${formatList(locationResult.stopPoints)}
""".trimIndent()

    private fun instantInformation(timeWithDuration: Instant) = formatter.format(timeWithDuration)

    private fun formatList(stops: List<TravelPoint>) = stops.joinToString("\n") { formatPoint(it) }

    private fun formatPoint(point: TravelPoint): String = """
Latitude: ${point.location.latitude}, Longitude: ${point.location.longitude}, time=${instantInformation(point.timeUtc)}
Commons template: {{Location|${point.location.latitude}|${point.location.longitude}}}
Google Maps Link: https://maps.google.com/maps?ll=${point.location.latitude},${point.location.longitude}&spn=0.01,0.01&t=h&q=${point.location.latitude},${point.location.longitude}
""".trimIndent()

}
