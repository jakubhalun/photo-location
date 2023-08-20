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

        // Handle namespaces for gx:coord within gx:Track
        val xPathCoord = document.createXPath("//gx:Track/gx:coord")
        xPathCoord.setNamespaceURIs(mapOf("gx" to "http://www.google.com/kml/ext/2.2"))
        val coords = xPathCoord.selectNodes(document).map { it.text.trim() }

        // Handle when elements within gx:Track using local-name to bypass namespaces
        val xPathWhen = document.createXPath("//gx:Track/*[local-name()='when']")
        val times = xPathWhen.selectNodes(document).map { it.text.trim() }

        if (coords.isEmpty()) {
            throw InvalidKmlInputFileException("Not a valid XML file - no gx:coord nodes")
        }

        if (coords.size != times.size) {
            throw InvalidKmlInputFileException("Mismatched gx:coord (${coords.size}) and when (${times.size}) elements count")
        }

        return coords.zip(times).map { (coordinates, time) ->
            val parts = coordinates.split(" ").map { it.toDouble() }
            TravelPoint(
                location = Location(latitude = parts[1], longitude = parts[0]),
                timeUtc = Instant.parse(time)
            )
        }
    }

}

data class Location(val latitude: Double, val longitude: Double)

data class TravelPoint(val location: Location, val timeUtc: Instant)

class InvalidKmlInputFileException(message: String): RuntimeException(message)
