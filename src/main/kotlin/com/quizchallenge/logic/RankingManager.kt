package com.quizchallenge.logic

import com.quizchallenge.data.model.EstatPartida
import com.quizchallenge.data.model.Jugador
import com.quizchallenge.data.model.Partida

/** Càlculs del rànquing; la persistència és responsabilitat del DAO. */
class RankingManager {
    fun obtenirRanking(jugadors: List<Jugador>): List<Jugador> =
        jugadors.sortedWith(compareByDescending<Jugador> { it.puntsAcumulats }.thenBy { it.nom.lowercase() })

    fun obtenirPosicio(jugadors: List<Jugador>, idJugador: Int): Int? {
        val index = obtenirRanking(jugadors).indexOfFirst { it.idJugador == idJugador }
        return if (index == -1) null else index + 1
    }

    fun obtenirRecord(partides: List<Partida>, idJugador: Int): Int =
        partides.filter { it.idJugador == idJugador && it.estat == EstatPartida.FINALITZADA }
            .maxOfOrNull { it.puntuacio } ?: 0

    fun afegirPunts(jugador: Jugador, partida: Partida): Jugador {
        require(partida.idJugador == jugador.idJugador) { "La partida no pertany a aquest jugador" }
        require(partida.estat == EstatPartida.FINALITZADA) { "La partida encara no ha acabat" }
        return jugador.copy(puntsAcumulats = jugador.puntsAcumulats + partida.puntuacio)
    }
}
