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
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.main.LocationInTimeTextProvider
import java.time.Duration
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
            val durationOffset = adjustTimeWithOffset()
            loadJpegData(db.files[0].absolutePath, durationOffset)
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
    }

    private fun adjustTimeWithOffset(): Duration {
        val offsetString = timeZoneOffsetComboBox.value
        val hours = offsetString.substring(0, 3).toInt()
        val minutes = offsetString.substring(4).toInt()

        return Duration.ofHours(hours.toLong()).plus(Duration.ofMinutes(minutes.toLong()))
    }

    private fun loadJpegData(path: String, durationOffset: Duration) {
        try {
            val creationTime = jpegReader.readCreationTime(path, durationOffset)
            updateOutput(creationTime)
        } catch (e: InvalidJpegInputFileException) {
            jpegInputArea.text = e.message
        }
    }

    @FXML
    fun onKmlDrop(dragEvent: DragEvent) {
        val db: Dragboard = dragEvent.dragboard
        if (db.hasFiles()) {
            outputTextArea.text = db.files[0].absolutePath
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
    }

    private fun updateOutput(time: Instant) {
        outputTextArea.text = locationInTimeTextProvider.textForTime(time)
    }
}
