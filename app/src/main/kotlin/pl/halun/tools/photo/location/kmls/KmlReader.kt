package pl.halun.tools.photo.location.kmls

import org.dom4j.DocumentHelper
import java.io.File
import java.time.Instant

class KmlReader {
    fun readTravelPoints(path: String): List<TravelPoint> {
        val file = File(path)
        if (!file.exists()) throw InvalidKmlInputFileException("File does not exist")
        if (file.isDirectory) throw InvalidKmlInputFileException("Single KML file is required, not a directory")

        val document = try {
            DocumentHelper.parseText(File(path).readText())
        } catch (e: Exception) {
            throw InvalidKmlInputFileException("Not a valid XML (KML) file")
        }
        val coords = document.selectNodes("//gx:coord").map { it.text.trim() }
        val times = document.selectNodes("//when").map { it.text.trim() }

        if (coords.isEmpty()) {
            throw InvalidKmlInputFileException("Not a valid XML file - no gx:coord nodes")
        }

        if (coords.size != times.size) {
            throw InvalidKmlInputFileException("Mismatched gx:coord and when elements count")
        }

        return coords.zip(times).map { (coordinates, time) ->
            val parts = coordinates.split(" ").map { it.toDouble() }
            TravelPoint(
                location = Location(longitude = parts[0], latitude = parts[1]),
                timeUtc = Instant.parse(time)
            )
        }
    }
}

data class Location(val longitude: Double, val latitude: Double)

data class TravelPoint(val location: Location, val timeUtc: Instant)

class InvalidKmlInputFileException(message: String): RuntimeException(message)
