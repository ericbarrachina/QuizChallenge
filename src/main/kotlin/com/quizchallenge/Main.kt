package com.quizchallenge

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application() {
    override fun start(primaryStage: Stage) {
        val fxmlLoader = FXMLLoader(Main::class.java.getResource("/views/menu.fxml"))
        val scene = Scene(fxmlLoader.load(), 1920.0, 1080.0)
        
        primaryStage.title = "Quiz Challenge"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(Main::class.java)
}