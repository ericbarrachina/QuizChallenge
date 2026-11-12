package com.quizchallenge.data.model

/** respostaCorrecta és un índex entre 0 i 3 (no entre 1 i 4). */
data class Pregunta(
    val idPregunta: Int,
    val idCategoria: Int,
    val enunciat: String,
    val opcions: List<String>,
    val respostaCorrecta: Int
) {
    fun validar() {
        require(idPregunta > 0) { "L'identificador de la pregunta ha de ser positiu" }
        require(idCategoria > 0) { "La categoria ha de ser positiva" }
        require(enunciat.isNotBlank()) { "L'enunciat no pot estar buit" }
        require(opcions.size == 4 && opcions.all { it.isNotBlank() }) {
            "Cada pregunta ha de tenir exactament 4 opcions amb text"
        }
        require(respostaCorrecta in 0..3) { "La resposta correcta ha d'estar entre 0 i 3" }
    }
}
