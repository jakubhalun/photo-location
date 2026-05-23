package pl.halun.tools.photo.location.main

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.HBox
import pl.halun.tools.photo.location.kmls.TravelPoint
import java.awt.Desktop
import java.net.URI
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class LocationResultNodeRenderer {
    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC)
    }

    fun renderNodes(result: LocationResult): List<Node> {
        val nodes = mutableListOf<Node>()

        nodes += copyAllButton(result)
        nodes += Separator()

        nodes += boldLabel("JPEG creation time (after applying zone change): ${formatter.format(result.photoTimeWithDuration)}")
        nodes += separator()

        nodes += boldLabel("Last point before:")
        nodes += renderPoint(result.lastBefore)
        nodes += separator()

        nodes += boldLabel("Point closest in time:")
        nodes += renderPoint(result.closestInTime)
        nodes += separator()

        nodes += boldLabel("Stops nearby (in time):")
        if (result.stopPoints.isEmpty()) {
            nodes += Label("(none)")
        } else {
            result.stopPoints.forEach { stop ->
                nodes += renderPoint(stop)
                nodes += separator()
            }
        }

        return nodes
    }

    private fun copyAllButton(result: LocationResult): Button =
        Button("📄 Copy all as text").apply {
            setOnAction {
                val content = ClipboardContent()
                content.putString(toPlainText(result))
                Clipboard.getSystemClipboard().setContent(content)
            }
        }

    private fun toPlainText(result: LocationResult): String = """
JPEG creation time (after applying zone change): ${formatter.format(result.photoTimeWithDuration)}

Last point before:
${formatPoint(result.lastBefore)}

Point closest in time:
${formatPoint(result.closestInTime)}

Stops nearby (in time):

${result.stopPoints.joinToString("\n") { formatPoint(it) }}
""".trimIndent()

    private fun formatPoint(point: TravelPoint): String {
        val lat = point.location.latitude
        val lon = point.location.longitude
        return """
Latitude: $lat, Longitude: $lon, time=${formatter.format(point.timeUtc)}
Commons template: {{Location|$lat|$lon}}
OpenStreetMap Link: https://www.openstreetmap.org/?mlat=$lat&mlon=$lon
Google Maps Link: https://maps.google.com/maps?ll=$lat,$lon&spn=0.01,0.01&t=h&q=$lat,$lon
""".trimIndent()
    }

    private fun renderPoint(point: TravelPoint): List<Node> {
        val lat = point.location.latitude
        val lon = point.location.longitude
        val template = "{{Location|$lat|$lon}}"
        val osmUrl = "https://www.openstreetmap.org/?mlat=$lat&mlon=$lon"
        val mapsUrl = "https://maps.google.com/maps?ll=$lat,$lon&spn=0.01,0.01&t=h&q=$lat,$lon"

        return listOf(
            Label("Latitude: $lat, Longitude: $lon, time=${formatter.format(point.timeUtc)}"),
            templateRow(template),
            hyperlink("OpenStreetMap ↗", osmUrl),
            hyperlink("Google Maps ↗", mapsUrl)
        )
    }

    private fun templateRow(template: String): HBox {
        val templateLabel = Label(template)
        val copyButton = Button("📋 Copy").apply {
            setOnAction {
                val content = ClipboardContent()
                content.putString(template)
                Clipboard.getSystemClipboard().setContent(content)
            }
        }
        return HBox(6.0, Label("Commons template:"), templateLabel, copyButton)
    }

    private fun hyperlink(text: String, url: String): Hyperlink =
        Hyperlink(text).apply {
            setOnAction {
                Thread {
                    Desktop.getDesktop().browse(URI(url))
                }.start()
            }
        }

    private fun boldLabel(text: String): Label =
        Label(text).apply {
            style = "-fx-font-weight: bold;"
        }

    private fun separator(): Label = Label("").apply { minHeight = 4.0 }
}
