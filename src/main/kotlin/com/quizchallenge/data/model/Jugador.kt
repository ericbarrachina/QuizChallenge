package com.quizchallenge.data.model

/** Jugador i puntuació total aconseguida en totes les partides finalitzades. */
data class Jugador(
    val idJugador: Int,
    val nom: String,
    val puntsAcumulats: Int = 0
)
