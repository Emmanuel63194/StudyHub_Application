package edu.unicauca.aplimovil.studyhub_application.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class RespuestaCuestionario(
    val texto: String,
    val esCorrecta: Boolean = false
)

data class PreguntaCuestionario(
    val texto: String,
    val respuestas: List<RespuestaCuestionario>
)

@Entity(tableName = "cuestionarios")
data class CuestionarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val titulo: String,

    val preguntas: List<PreguntaCuestionario> = emptyList()
) {
    val cantidadPreguntas: Int get() = preguntas.size
}