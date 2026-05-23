package pl.halun.tools.photo.location

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.VBox
import javafx.stage.Stage
import pl.halun.tools.photo.location.jpegs.InvalidJpegInputFileException
import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.InvalidKmlInputFileException
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.kmls.TravelPoint
import pl.halun.tools.photo.location.main.*
import java.time.Instant

private const val MAX_TITLE_LENGTH = 180

class MainWindowController {

    private val locationInTimeProvider: LocationInTimeProvider
    private val jpegReader: JpegReader
    private val kmlReader: KmlReader
    private val resultRenderer: LocationResultNodeRenderer
    init {
        val configuration = Configuration()
        locationInTimeProvider = configuration.locationInTimeTextProvider()
        jpegReader = configuration.jpegReader()
        kmlReader = configuration.kmlReader()
        resultRenderer = LocationResultNodeRenderer()
    }

    @FXML
    lateinit var jpegInputArea: TextArea

    @FXML
    lateinit var kmlInputArea: TextArea

    @FXML
    lateinit var outputContainer: VBox

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
            val creationTime = jpegReader.getOriginalCreationDate(path)
            updateOutput(creationTime)
            updateWindowForSuccessfulJpegRead(path)
        } catch (e: InvalidJpegInputFileException) {
            updateWindowForFailureInJpegRead(e.message)
        }

    private fun updateOutput(time: Instant) {
        updateOutputDisplay(locationInTimeProvider.resultForChangedTime(time))
    }

    private fun updateWindowForSuccessfulJpegRead(path: String) {
        jpegInputArea.text = locationInTimeProvider.loadedTime()
        val stage = jpegInputArea.scene.window as Stage
        stage.title = "$DEFAULT_WINDOW_NAME: $path".take(MAX_TITLE_LENGTH)
    }

    private fun updateWindowForFailureInJpegRead(errorMessage: String?) {
        jpegInputArea.text = errorMessage ?: "Error while reading JPEG file"
        val stage = jpegInputArea.scene.window as Stage
        stage.title = DEFAULT_WINDOW_NAME
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
                    kmlInputArea.text = "Loaded ${locationInTimeProvider.numberOfTravelPoints()} travel points"
                }
            } catch (e: InvalidKmlInputFileException) {
                Platform.runLater { kmlInputArea.text = e.message }
            }
        }.start()

    private fun updateOutput(travelPoints: List<TravelPoint>) {
        updateOutputDisplay(locationInTimeProvider.resultForChangedLocations(travelPoints))
    }

    @FXML
    fun handleComboBoxChange() {
        updateOutputDisplay(locationInTimeProvider.resultForChangedDifferenceToUtc(timeZoneOffsetComboBox.value))
    }

    private fun updateOutputDisplay(result: Result) {
        outputContainer.children.setAll(
            when (result) {
                is InvalidResult -> listOf(Label(result.message))
                is LocationResult -> resultRenderer.renderNodes(result)
            }
        )
    }
}
