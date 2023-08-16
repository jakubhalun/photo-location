package pl.halun.tools.photo.location

import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.text.TextFlow

class MainWindowController {

    @FXML
    lateinit var formattedTextArea: TextArea

    @FXML
    fun onJpegDrop(dragEvent: javafx.scene.input.DragEvent) {
        val db: Dragboard = dragEvent.dragboard
        if (db.hasFiles()) {
            // Zrób coś z przeciągniętym plikiem JPEG, na przykład:
            formattedTextArea.text = db.files[0].absolutePath
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
    }

    @FXML
    fun onKmlDrop(dragEvent: javafx.scene.input.DragEvent) {
        val db: Dragboard = dragEvent.dragboard
        if (db.hasFiles()) {
            // Zrób coś z przeciągniętym plikiem KML, na przykład:
            formattedTextArea.text = db.files[0].absolutePath
        }
        dragEvent.isDropCompleted = true
        dragEvent.consume()
    }

    @FXML
    fun onDragOver(dragEvent: javafx.scene.input.DragEvent) {
        if (dragEvent.gestureSource != null &&
                dragEvent.dragboard.hasFiles()
        ) {
            dragEvent.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
        }
        dragEvent.consume()
    }
}
