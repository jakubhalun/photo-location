package pl.halun.tools.photo.location

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.web.WebView
import pl.halun.tools.photo.location.jpegs.InvalidJpegInputFileException
import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.InvalidKmlInputFileException
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.kmls.TravelPoint
import pl.halun.tools.photo.location.main.LocationInTimeTextProvider
import java.awt.Desktop
import java.net.URI
import java.time.Instant

class MainWindowController {

    private val locationInTimeTextProvider: LocationInTimeTextProvider
    private val jpegReader: JpegReader
    private val kmlReader: KmlReader
    init {
        val configuration = Configuration()
        locationInTimeTextProvider = configuration.locationInTimeTextProvider()
        jpegReader = configuration.jpegReader()
        kmlReader = configuration.kmlReader()
    }

    @FXML
    lateinit var jpegInputArea: TextArea

    @FXML
    lateinit var kmlInputArea: TextArea

    @FXML
    lateinit var outputWebView: WebView

    @FXML
    lateinit var timeZoneOffsetComboBox: ComboBox<String>

    val timeZoneOffsets: ObservableList<String> = FXCollections.observableArrayList<String>().apply {
        for (i in -12..14) {
            add(String.format("%+d:00", i))
            if (i != 12 && i != 13) {
                add(String.format("%+d:30", i))
            }
        }
    }

    private fun attachExternalLinkOpener() {
        outputWebView.engine.locationProperty().addListener { _, oldValue, newValue ->
            if (newValue != null && newValue != oldValue) {
                outputWebView.engine.load("")  // Prevent WebView from navigating
                Platform.runLater {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI(newValue))
                    }
                }
            }
        }
    }

    @FXML
    fun onJpegDragOver(dragEvent: DragEvent) {
        if (dragEvent.dragboard.hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY)
        }
        dragEvent.consume()
    }

    @FXML
    fun onKmlDragOver(dragEvent: DragEvent) {
        if (dragEvent.dragboard.hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY)
        }
        dragEvent.consume()
    }

    @FXML
    fun onJpegDrop(dragEvent: DragEvent) {
        val db: Dragboard = dragEvent.dragboard
        if (db.hasFiles()) {
            loadJpegData(db.files[0].absolutePath)
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
    }

    private fun loadJpegData(path: String) =
        try {
            val creationTime = jpegReader.readCreationTime(path)
            updateOutput(creationTime)
            jpegInputArea.text = locationInTimeTextProvider.loadedTime()
        } catch (e: InvalidJpegInputFileException) {
            jpegInputArea.text = e.message
        }

    private fun updateOutput(time: Instant) {
        val content = locationInTimeTextProvider.textForChangedTime(time)
        outputWebView.engine.loadContent(html(content))
        attachExternalLinkOpener()
    }

    @FXML
    fun onKmlDrop(dragEvent: DragEvent) {
        val db: Dragboard = dragEvent.dragboard
        if (db.hasFiles()) {
            kmlInputArea.text = "Loading..."
            loadKmlData(db.files[0].absolutePath)
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
    }

    private fun loadKmlData(path: String) =
        Thread {
            try {
                val travelPoints = kmlReader.readTravelPoints(path)
                Platform.runLater {
                    updateOutput(travelPoints)
                    kmlInputArea.text = "Loaded ${locationInTimeTextProvider.numberOfTravelPoints()} travel points"
                }
            } catch (e: InvalidKmlInputFileException) {
                Platform.runLater { kmlInputArea.text = e.message }
            }
        }.start()

    private fun updateOutput(travelPoints: List<TravelPoint>) {
        val content = locationInTimeTextProvider.textForChangedLocations(travelPoints)
        outputWebView.engine.loadContent(html(content))
        attachExternalLinkOpener()
    }

    @FXML
    fun handleComboBoxChange() {
        val content = locationInTimeTextProvider.textForChangedDifferenceToUtc(timeZoneOffsetComboBox.value)
        outputWebView.engine.loadContent(html(content))
        attachExternalLinkOpener()
    }

    private fun html(content: String): String =
            """
                <html><body><p style="font-family:verdana">$content</p></body></html>
            """.trimIndent()
}
