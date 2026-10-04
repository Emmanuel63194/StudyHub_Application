package edu.unicauca.aplimovil.studyhub_application.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eventos")
data class CalendarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val titulo: String,

    val fecha: Long,

    val hora: Int,

    val nota: String
)