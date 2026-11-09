package com.quizchallenge.ui.controllers

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.image.ImageView

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
        // Aquí Manel connectarà la lògica de la partida més endavant
    }

    @FXML
    fun handleVeureRanking() {
        println("Botó de rànquing premut")
        // Aquí s'obrirà la vista del rànquing
    }
}