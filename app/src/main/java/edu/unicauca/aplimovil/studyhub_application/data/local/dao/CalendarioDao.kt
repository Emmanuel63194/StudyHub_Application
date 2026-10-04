package edu.unicauca.aplimovil.studyhub_application.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(evento: CalendarioEntity): Long

    @Update
    suspend fun actualizar(evento: CalendarioEntity)

    @Delete
    suspend fun eliminar(evento: CalendarioEntity)

    @Query("SELECT * FROM eventos ORDER BY fecha ASC, hora ASC, id ASC")
    fun obtenerTodos(): Flow<List<CalendarioEntity>>

    @Query("SELECT * FROM eventos WHERE id = :eventoId")
    suspend fun obtenerPorId(eventoId: Long): CalendarioEntity?

    // Se vuelva a obtener los eventos una vez se reinicia el dispositivo (osea para reprogramar notificaciones)
    @Query("SELECT * FROM eventos")
    suspend fun obtenerTodosUnaVez(): List<CalendarioEntity>
}