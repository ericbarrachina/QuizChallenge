package com.quizchallenge.data.model

import java.time.LocalDateTime

enum class EstatPartida { EN_CURS, FINALITZADA }

/** Identificador 0: partida que encara no s'ha desat a MariaDB. */
data class Partida(
    val idPartida: Int = 0,
    val idJugador: Int,
    val idCategoria: Int,
    val puntuacio: Int = 0,
    val data: String = LocalDateTime.now().toString(),
    val estat: EstatPartida = EstatPartida.EN_CURS
)
