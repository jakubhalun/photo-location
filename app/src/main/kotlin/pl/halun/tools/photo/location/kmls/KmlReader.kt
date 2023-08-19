package pl.halun.tools.photo.location.kmls

import java.time.Instant

class KmlReader {
    fun readTravelPoints(path: String): List<TravelPoint> {
        // TODO
        return emptyList()
    }
}

data class Location(val longitude: Double, val latitude: Double)

data class TravelPoint(val location: Location, val timeUtc: Instant)

class InvalidKmlInputFileException(message: String): RuntimeException(message)
