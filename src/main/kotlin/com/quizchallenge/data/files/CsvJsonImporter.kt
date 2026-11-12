package com.quizchallenge.data.files

import com.google.gson.reflect.TypeToken
import com.quizchallenge.data.model.Pregunta
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/** Importació CSV -> objectes Kotlin -> JSON (índex de resposta correcta: 0..3). */
class CsvJsonImporter(private val fitxers: FileManager = FileManager()) {
    private val tipusLlista = object : TypeToken<List<Pregunta>>() {}.type

    fun carregarPreguntesJson(ruta: Path): List<Pregunta> =
        validarPreguntes(fitxers.llegirJson(ruta, tipusLlista))

    fun carregarPreguntesInicials(): List<Pregunta> =
        validarPreguntes(fitxers.llegirRecursJson("/data/preguntes.json", tipusLlista))

    fun importarCsv(ruta: Path): List<Pregunta> {
        require(Files.isRegularFile(ruta)) { "No existeix el CSV: $ruta" }
        val text = Files.readString(ruta, StandardCharsets.UTF_8).removePrefix("\uFEFF")
        val capcalera = text.lineSequence().firstOrNull()?.trim().orEmpty()
        val separador = if (capcalera.count { it == ';' } > capcalera.count { it == ',' }) ';' else ','
        val files = llegirCsv(text, separador).filterNot { camps -> camps.all { it.isBlank() } }
        require(files.isNotEmpty()) { "El CSV és buit" }
        val columnes = files.first().map { it.trim().lowercase().replace("_", "") }
        val esperades = listOf("idpregunta", "idcategoria", "enunciat", "opcio1", "opcio2", "opcio3", "opcio4", "respostacorrecta")
        require(columnes == esperades) { "Capçalera CSV incorrecta. Esperada: ${esperades.joinToString(separador.toString())}" }
        val preguntes = files.drop(1).mapIndexed { i, fila ->
            require(fila.size == esperades.size) { "Nombre de columnes incorrecte a la fila ${i + 2}" }
            try {
                Pregunta(
                    idPregunta = fila[0].trim().toInt(),
                    idCategoria = fila[1].trim().toInt(),
                    enunciat = fila[2].trim(),
                    opcions = fila.subList(3, 7).map { it.trim() },
                    respostaCorrecta = fila[7].trim().toInt()
                ).also { it.validar() }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Identificador o resposta incorrectes a la fila ${i + 2}", e)
            }
        }
        return validarPreguntes(preguntes)
    }

    fun convertirCsvAJson(origenCsv: Path, destiJson: Path): List<Pregunta> {
        val preguntes = importarCsv(origenCsv)
        fitxers.guardarJson(destiJson, preguntes)
        return preguntes
    }

    private fun validarPreguntes(preguntes: List<Pregunta>): List<Pregunta> {
        preguntes.forEach { it.validar() }
        require(preguntes.map { it.idPregunta }.distinct().size == preguntes.size) {
            "Hi ha identificadors de pregunta duplicats"
        }
        return preguntes
    }

    /** Cometes dobles, comes/punt i coma i salts de línia dins de camps entre cometes. */
    private fun llegirCsv(text: String, separador: Char): List<List<String>> {
        val files = mutableListOf<List<String>>()
        val fila = mutableListOf<String>()
        val camp = StringBuilder()
        var entreCometes = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' && entreCometes && i + 1 < text.length && text[i + 1] == '"' -> {
                    camp.append('"')
                    i++
                }
                c == '"' -> entreCometes = !entreCometes
                c == separador && !entreCometes -> {
                    fila.add(camp.toString()); camp.setLength(0)
                }
                (c == '\n' || c == '\r') && !entreCometes -> {
                    fila.add(camp.toString()); files.add(fila.toList())
                    fila.clear(); camp.setLength(0)
                    if (c == '\r' && i + 1 < text.length && text[i + 1] == '\n') i++
                }
                else -> camp.append(c)
            }
            i++
        }
        require(!entreCometes) { "CSV incorrecte: cometes sense tancar" }
        if (camp.isNotEmpty() || fila.isNotEmpty()) {
            fila.add(camp.toString()); files.add(fila.toList())
        }
        return files
    }
}
