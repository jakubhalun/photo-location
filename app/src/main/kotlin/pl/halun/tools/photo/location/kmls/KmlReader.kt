package pl.halun.tools.photo.location.kmls

import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants

class KmlReader {

    fun readTravelPoints(path: String): List<TravelPoint> {
        val file = getAndValidateFile(path)
        return getTravelPointsFromKmlTrack(file)
    }

    private fun getAndValidateFile(path: String): File {
        val file = File(path)
        if (!file.exists()) throw InvalidKmlInputFileException("File does not exist")
        if (file.isDirectory) throw InvalidKmlInputFileException("Single KML file is required, not a directory")
        checkFileFormat(path)
        return file
    }

    private fun checkFileFormat(path: String) {
        try {
            Files.lines(Paths.get(path)).use { lines ->
                val firstNonEmptyLine = lines
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("<?xml") }
                    .findFirst()

                if (firstNonEmptyLine.isPresent) {
                    val line = firstNonEmptyLine.get()
                    if (!line.startsWith("<kml", ignoreCase = true)) {
                        throw InvalidKmlInputFileException("File is not a valid KML file (missing <kml> root element)")
                    }
                } else {
                    throw InvalidKmlInputFileException("File is empty or not a valid KML file")
                }
            }
        } catch (e: Exception) {
            throw InvalidKmlInputFileException("File encoding is invalid or file is not a text file")
        }
    }

    private fun getTravelPointsFromKmlTrack(file: File): List<TravelPoint> {
        val travelPoints = mutableListOf<TravelPoint>()
        val inputFactory = XMLInputFactory.newInstance()
        FileInputStream(file).use { inputStream ->
            val reader = inputFactory.createXMLStreamReader(inputStream)

            var inGxTrack = false
            val gxNamespaceURI = "http://www.google.com/kml/ext/2.2"
            val kmlNamespaceURI = "http://www.opengis.net/kml/2.2"

            val coordinates = mutableListOf<String>()
            val times = mutableListOf<String>()

            while (reader.hasNext()) {
                when (reader.next()) {
                    XMLStreamConstants.START_ELEMENT -> {
                        val localName = reader.localName
                        val namespaceURI = reader.namespaceURI ?: ""

                        if (localName == "Track" && namespaceURI == gxNamespaceURI) {
                            inGxTrack = true
                        } else if (inGxTrack) {
                            if (localName == "coord" && namespaceURI == gxNamespaceURI) {
                                val coordinatesText = reader.elementText.trim()
                                coordinates.add(coordinatesText)
                            } else if (localName == "when" && namespaceURI == kmlNamespaceURI) {
                                val whenText = reader.elementText.trim()
                                times.add(whenText)
                            }
                        }
                    }

                    XMLStreamConstants.END_ELEMENT -> {
                        val localName = reader.localName
                        val namespaceURI = reader.namespaceURI ?: ""

                        if (localName == "Track" && namespaceURI == gxNamespaceURI) {
                            inGxTrack = false

                            if (coordinates.size != times.size) {
                                println(coordinates.size)
                                println(times.size)
                                throw InvalidKmlInputFileException("Mismatched gx:coord and when elements count")
                            }

                            travelPoints.addAll(coordinates.zip(times).map { (coordinates, time) ->
                                val parts = coordinates.split(" ").map { it.toDouble() }
                                TravelPoint(
                                    location = Location(latitude = parts[1], longitude = parts[0]),
                                    timeUtc = Instant.parse(time)
                                )
                            })

                            coordinates.clear()
                            times.clear()
                        }
                    }
                }
            }
            reader.close()
        }
        return travelPoints
    }
}

data class Location(val latitude: Double, val longitude: Double)

data class TravelPoint(val location: Location, val timeUtc: Instant)

class InvalidKmlInputFileException(message: String) : RuntimeException(message)
