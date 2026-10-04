package edu.unicauca.aplimovil.studyhub_application.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calificaciones")
data class CalificacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val asignaturaId: Int,
    val nota: Double,
    val porcentaje: Int,
    val descripcion: String,
    val fecha: Long
)