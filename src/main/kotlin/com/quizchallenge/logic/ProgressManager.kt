package com.quizchallenge.logic

import com.quizchallenge.data.files.FileManager
import com.quizchallenge.data.model.EstatPartida
import com.quizchallenge.data.model.Partida
import com.quizchallenge.data.model.Progres
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

/** Estat JSON d'una partida (els fitxers es creen al directori de dades, no a resources). */
data class PartidaDesada(val partida: Partida, val progres: Progres)

class ProgressManager(
    private val fitxers: FileManager = FileManager(),
    private val carpetaDades: Path = Path.of(System.getProperty("user.home"), ".quizchallenge")
) {
    private fun rutaProgres(idJugador: Int): Path = carpetaDades.resolve("progres_jugador_$idJugador.json")

    fun guardarPartidaEnCurs(partida: Partida, progres: Progres) {
        require(partida.estat == EstatPartida.EN_CURS) { "Només es pot guardar com a progrés una partida en curs" }
        require(partida.idPartida == progres.idPartida) { "Els ID de partida no coincideixen" }
        fitxers.guardarJson(rutaProgres(partida.idJugador), PartidaDesada(partida, progres))
    }

    fun carregarPartidaEnCurs(idJugador: Int): PartidaDesada? {
        val ruta = rutaProgres(idJugador)
        if (!Files.exists(ruta)) return null
        val estat = fitxers.llegirJson<PartidaDesada>(ruta, PartidaDesada::class.java)
        require(estat.partida.idJugador == idJugador && estat.partida.estat == EstatPartida.EN_CURS) {
            "El progrés desat no correspon al jugador o a una partida en curs"
        }
        return estat
    }

    fun eliminarPartidaEnCurs(idJugador: Int): Boolean = fitxers.eliminarFitxer(rutaProgres(idJugador))

    fun guardarHistorial(partida: Partida): Path {
        require(partida.estat == EstatPartida.FINALITZADA) { "Només es desen partides finalitzades" }
        val id = if (partida.idPartida > 0) partida.idPartida.toString() else UUID.randomUUID().toString()
        val ruta = carpetaDades.resolve("historial").resolve("partida_$id.json")
        fitxers.guardarJson(ruta, partida)
        return ruta
    }

    fun carregarHistorial(): List<Partida> {
        val carpeta = carpetaDades.resolve("historial")
        if (!Files.isDirectory(carpeta)) return emptyList()
        return Files.list(carpeta).use { stream ->
            stream.filter { Files.isRegularFile(it) && it.fileName.toString().endsWith(".json") }
                .sorted()
                .map { fitxers.llegirJson<Partida>(it, Partida::class.java) }
                .toList()
        }
    }
}
