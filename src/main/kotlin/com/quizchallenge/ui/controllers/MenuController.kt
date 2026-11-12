package com.quizchallenge.ui.controllers


import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.stage.Stage


class MenuController {


    @FXML
    lateinit var imgLogo: ImageView


    @FXML
    lateinit var txtNomJugador: TextField


    @FXML
    lateinit var cbCategoria: ComboBox<String>


    @FXML
    lateinit var btnIniciar: Button


    @FXML
    lateinit var btnRanking: Button


    @FXML
    fun initialize() {
        // Omplim el ComboBox amb les categories de la vostra proposta (Cultura general, Esports, Geografia)
        cbCategoria.items.addAll("Cultura general", "Esports", "Geografia")
    }


    @FXML
    fun handleIniciarPartida() {
        val nom = txtNomJugador.text
        val categoria = cbCategoria.value
        println("Usuari: $nom | Categoria triada: $categoria")
        // Aquí Manel connectarà la càrrega de la partida amb les dades del jugador i la categoria triada desde el backend
    }


    @FXML
    fun handleVeureRanking() {
        val stage = btnRanking.scene.window as Stage
        val fxmlLoader = FXMLLoader(javaClass.getResource("/views/ranking.fxml"))
        stage.scene = Scene(fxmlLoader.load(), 1920.0, 1080.0)
    }
}