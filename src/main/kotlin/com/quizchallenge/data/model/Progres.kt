package com.quizchallenge.data.model

/** Permet continuar una ronda conservant l'ordre original de les preguntes. */
data class Progres(
    val idPartida: Int,
    val preguntesSeleccionades: List<Int>,
    val indexPreguntaActual: Int = 0,
    val respostesDonades: List<Int> = emptyList(),
    val puntsActuals: Int = 0
)
