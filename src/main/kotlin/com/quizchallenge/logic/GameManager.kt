package com.quizchallenge.logic

import com.quizchallenge.data.model.EstatPartida
import com.quizchallenge.data.model.Partida
import com.quizchallenge.data.model.Pregunta
import com.quizchallenge.data.model.Progres
import kotlin.random.Random

/**
 * Regles del joc, sense dependències de JavaFX, de fitxers o de base de dades.
 * Les respostes es passen com a índex (0, 1, 2 o 3).
 */
class GameManager(
    private val preguntesDisponibles: List<Pregunta>,
    private val random: Random = Random.Default
) {
    private var partida: Partida? = null
    private var progres: Progres? = null
    private val preguntesPerId: Map<Int, Pregunta>

    init {
        preguntesDisponibles.forEach { it.validar() }
        require(preguntesDisponibles.map { it.idPregunta }.distinct().size == preguntesDisponibles.size) {
            "Hi ha identificadors de preguntes repetits"
        }
        preguntesPerId = preguntesDisponibles.associateBy { it.idPregunta }
    }

    val puntuacio: Int get() = progres?.puntsActuals ?: 0
    val preguntaActual: Int get() = progres?.indexPreguntaActual ?: 0

    fun iniciarPartida(idJugador: Int, idCategoria: Int, idPartida: Int = 0): Partida {
        require(idJugador > 0) { "Cal indicar un jugador existent" }
        require(idCategoria > 0) { "La categoria ha de ser positiva" }
        require(idPartida >= 0) { "L'identificador de partida no pot ser negatiu" }

        val candidates = preguntesDisponibles.filter { it.idCategoria == idCategoria }
        require(candidates.size >= PREGUNTES_PER_PARTIDA) {
            "La categoria $idCategoria necessita com a mínim $PREGUNTES_PER_PARTIDA preguntes"
        }
        val seleccionades = candidates.shuffled(random).take(PREGUNTES_PER_PARTIDA)
        partida = Partida(idPartida = idPartida, idJugador = idJugador, idCategoria = idCategoria)
        progres = Progres(idPartida = idPartida, preguntesSeleccionades = seleccionades.map { it.idPregunta })
        return obtenirPartida()
    }

    fun obtenirPreguntaActual(): Pregunta? {
        val estat = progres ?: return null
        if (estat.indexPreguntaActual >= estat.preguntesSeleccionades.size) return null
        return preguntesPerId[estat.preguntesSeleccionades[estat.indexPreguntaActual]]
    }

    fun respondrePregunta(resposta: Int): Boolean {
        require(resposta in 0..3) { "La resposta ha de ser un índex entre 0 i 3" }
        val actual = partida ?: error("Primer has d'iniciar una partida")
        check(actual.estat == EstatPartida.EN_CURS) { "La partida ja ha finalitzat" }
        val pregunta = obtenirPreguntaActual() ?: error("No queden preguntes per respondre")
        val estat = progres ?: error("No hi ha progrés de partida")

        val encert = resposta == pregunta.respostaCorrecta
        val nousPunts = estat.puntsActuals + if (encert) PUNTS_PER_ENCERT else 0
        val nouIndex = estat.indexPreguntaActual + 1
        progres = estat.copy(
            indexPreguntaActual = nouIndex,
            respostesDonades = estat.respostesDonades + resposta,
            puntsActuals = nousPunts
        )
        partida = actual.copy(
            puntuacio = nousPunts,
            estat = if (nouIndex == PREGUNTES_PER_PARTIDA) EstatPartida.FINALITZADA else EstatPartida.EN_CURS
        )
        return encert
    }

    fun partidaAcabada(): Boolean = partida?.estat == EstatPartida.FINALITZADA

    fun obtenirPartida(): Partida = partida ?: error("No hi ha cap partida iniciada")

    fun obtenirProgres(): Progres {
        val estat = progres ?: error("No hi ha cap partida iniciada")
        return estat.copy(
            preguntesSeleccionades = estat.preguntesSeleccionades.toList(),
            respostesDonades = estat.respostesDonades.toList()
        )
    }

    /** Restaura les dades guardades en JSON i comprova que siguin coherents. */
    fun recuperarPartida(partidaDesada: Partida, progresDesat: Progres) {
        require(partidaDesada.idJugador > 0 && partidaDesada.idCategoria > 0) { "Partida invàlida" }
        require(partidaDesada.idPartida == progresDesat.idPartida) { "Els ID de partida no coincideixen" }
        val ids = progresDesat.preguntesSeleccionades
        require(ids.size == PREGUNTES_PER_PARTIDA && ids.distinct().size == PREGUNTES_PER_PARTIDA) {
            "La partida desada no conté 10 preguntes diferents"
        }
        val seleccionades = ids.map { id ->
            val pregunta = preguntesPerId[id] ?: throw IllegalArgumentException("Falta la pregunta $id")
            require(pregunta.idCategoria == partidaDesada.idCategoria) { "Categoria incorrecta en la pregunta $id" }
            pregunta
        }
        require(progresDesat.indexPreguntaActual in 0..PREGUNTES_PER_PARTIDA) { "Índex de progrés invàlid" }
        require(progresDesat.respostesDonades.size == progresDesat.indexPreguntaActual) {
            "El nombre de respostes no coincideix amb el progrés"
        }
        require(progresDesat.respostesDonades.all { it in 0..3 }) { "Hi ha respostes invàlides" }
        val puntsCalculats = progresDesat.respostesDonades.mapIndexed { i, resposta ->
            if (seleccionades[i].respostaCorrecta == resposta) PUNTS_PER_ENCERT else 0
        }.sum()
        require(partidaDesada.puntuacio == puntsCalculats && progresDesat.puntsActuals == puntsCalculats) {
            "La puntuació desada no correspon a les respostes"
        }
        val estatEsperat = if (progresDesat.indexPreguntaActual == PREGUNTES_PER_PARTIDA) {
            EstatPartida.FINALITZADA
        } else EstatPartida.EN_CURS
        require(partidaDesada.estat == estatEsperat) { "Estat de partida inconsistent" }
        partida = partidaDesada
        progres = progresDesat.copy(
            preguntesSeleccionades = ids.toList(),
            respostesDonades = progresDesat.respostesDonades.toList()
        )
    }

    companion object {
        const val PREGUNTES_PER_PARTIDA = 10
        const val PUNTS_PER_ENCERT = 10
    }
}
