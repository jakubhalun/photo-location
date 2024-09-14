package pl.halun.tools.photo.location

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

const val DEFAULT_WINDOW_NAME = "Photo Location (JPEG to KML Matcher)"

class App : Application() {
    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(App::class.java.getResource("/MainWindow.fxml"))
        val root: Parent = loader.load()

        primaryStage.title = DEFAULT_WINDOW_NAME
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }
}

fun main() {
    Application.launch(App::class.java)
}
