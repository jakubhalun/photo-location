package pl.halun.tools.photo.location

import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.main.LocationInTimeTextProvider

class MainWindowController(
    private val locationInTimeTextProvider: LocationInTimeTextProvider,
    private val jpegReader: JpegReader,
    private val kmlReader: KmlReader
) {

    @FXML
    lateinit var outputTextArea: TextArea

    @FXML
    fun onJpegDrop(dragEvent: DragEvent) {
        val db: Dragboard = dragEvent.dragboard
        if (db.hasFiles()) {
            outputTextArea.text = db.files[0].absolutePath
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
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
}
