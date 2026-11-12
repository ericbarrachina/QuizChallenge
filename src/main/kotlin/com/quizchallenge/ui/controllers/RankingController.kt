package com.quizchallenge.ui.controllers


import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.stage.Stage


class RankingController {


    @FXML lateinit var imgLogo: ImageView
    @FXML lateinit var cbFiltreCategoria: ComboBox<String>
    @FXML lateinit var tblRanking: TableView<Any>
    @FXML lateinit var colPosicio: TableColumn<Any, String>
    @FXML lateinit var colJugador: TableColumn<Any, String>
    @FXML lateinit var colCategoria: TableColumn<Any, String>
    @FXML lateinit var colPuntuacio: TableColumn<Any, String>
    @FXML lateinit var btnTornar: Button


    @FXML
    fun initialize() {
        cbFiltreCategoria.items.addAll("Totes les categories", "Cultura general", "Esports", "Geografia")
        cbFiltreCategoria.selectionModel.selectFirst()
        tblRanking.placeholder = Label("Encara no hi ha puntuacions registrades.")
        // Aquí Manel connectarà la càrrega del rànquing des del fitxer/dades
    }


    @FXML
    fun handleTornarMenu() {
        val stage = btnTornar.scene.window as Stage
        val fxmlLoader = FXMLLoader(javaClass.getResource("/views/menu.fxml"))
        stage.scene = Scene(fxmlLoader.load(), 1920.0, 1080.0)
    }
}