package edu.unicauca.aplimovil.studyhub_application.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una respuesta de una pregunta. Solo texto.
 */
data class RespuestaCuestionario(
    val texto: String,
    val esCorrecta: Boolean = false
)

/**
 * Una pregunta de un cuestionario. Solo texto.
 *
 * Tiene 2 o 3 respuestas: las dos primeras son obligatorias y la
 * tercera es opcional. Al menos una respuesta debe ser correcta.
 */
data class PreguntaCuestionario(
    val texto: String,
    val respuestas: List<RespuestaCuestionario>
)

/**
 * Cuestionario de StudyHub. Toda su información (título, preguntas y
 * respuestas) se almacena en una única fila; las preguntas se guardan
 * como JSON mediante Converters.
 */
@Entity(tableName = "cuestionarios")
data class CuestionarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val titulo: String,

    val preguntas: List<PreguntaCuestionario> = emptyList()
) {
    val cantidadPreguntas: Int get() = preguntas.size
}