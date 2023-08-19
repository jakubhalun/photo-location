package pl.halun.tools.photo.location

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import pl.halun.tools.photo.location.jpegs.InvalidJpegInputFileException
import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.InvalidKmlInputFileException
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.kmls.TravelPoint
import pl.halun.tools.photo.location.main.LocationInTimeTextProvider
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
    lateinit var outputTextArea: TextArea

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
            val creationTime = jpegReader.readCreationTime(path)
            updateOutput(creationTime)
        } catch (e: InvalidJpegInputFileException) {
            jpegInputArea.text = e.message
        }

    private fun updateOutput(time: Instant) {
        outputTextArea.text = locationInTimeTextProvider.textForChangedTime(time)
    }

    @FXML
    fun onKmlDrop(dragEvent: DragEvent) {
        val db: Dragboard = dragEvent.dragboard
        if (db.hasFiles()) {
            loadKmlData(db.files[0].absolutePath)
            // outputTextArea.text =
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
    }

    private fun loadKmlData(path: String) =
        try {
            val travelPoints = kmlReader.readTravelPoints(path)
            updateOutput(travelPoints)
        } catch (e: InvalidKmlInputFileException) {
            outputTextArea.text = e.message
        }

    private fun updateOutput(travelPoints: List<TravelPoint>) {
        outputTextArea.text = locationInTimeTextProvider.textForChangedLocations(travelPoints)
    }

    @FXML
    fun handleComboBoxChange() {
        outputTextArea.text = locationInTimeTextProvider.textForChangedDifferenceToUtc(timeZoneOffsetComboBox.value)
    }
}
