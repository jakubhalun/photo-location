package pl.halun.tools.photo.location

import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard

class MainWindowController {

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
