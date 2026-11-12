package com.quizchallenge.data.files

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/** Operacions amb fitxers i serialització JSON. No depèn de la interfície. */
class FileManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun crearCarpeta(ruta: Path): Path = Files.createDirectories(ruta)

    fun guardarJson(ruta: Path, dades: Any) {
        ruta.parent?.let { crearCarpeta(it) }
        val temporal = Files.createTempFile(ruta.parent ?: Path.of("."), "quizchallenge-", ".tmp")
        try {
            Files.newBufferedWriter(temporal, StandardCharsets.UTF_8).use { it.write(gson.toJson(dades)) }
            Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING)
        } finally {
            Files.deleteIfExists(temporal)
        }
    }

    fun <T> llegirJson(ruta: Path, tipus: Type): T {
        require(Files.isRegularFile(ruta)) { "No existeix el fitxer: $ruta" }
        Files.newBufferedReader(ruta, StandardCharsets.UTF_8).use {
            return gson.fromJson<T>(it, tipus) ?: throw IllegalArgumentException("JSON buit o incorrecte: $ruta")
        }
    }

    fun <T> llegirRecursJson(rutaRecurs: String, tipus: Type): T {
        val stream = javaClass.getResourceAsStream(rutaRecurs)
            ?: throw IllegalArgumentException("No existeix el recurs: $rutaRecurs")
        stream.bufferedReader(StandardCharsets.UTF_8).use {
            return gson.fromJson<T>(it, tipus) ?: throw IllegalArgumentException("JSON buit: $rutaRecurs")
        }
    }

    fun copiarFitxer(origen: Path, desti: Path): Path {
        require(Files.isRegularFile(origen)) { "No existeix el fitxer: $origen" }
        desti.parent?.let { crearCarpeta(it) }
        return Files.copy(origen, desti, StandardCopyOption.REPLACE_EXISTING)
    }

    fun moureFitxer(origen: Path, desti: Path): Path {
        require(Files.isRegularFile(origen)) { "No existeix el fitxer: $origen" }
        desti.parent?.let { crearCarpeta(it) }
        return Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING)
    }

    fun eliminarFitxer(ruta: Path): Boolean = Files.deleteIfExists(ruta)
}
