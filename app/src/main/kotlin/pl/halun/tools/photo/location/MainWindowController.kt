package pl.halun.tools.photo.location

import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import pl.halun.tools.photo.location.jpegs.InvalidJpegInputFileException
import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.KmlReader
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
