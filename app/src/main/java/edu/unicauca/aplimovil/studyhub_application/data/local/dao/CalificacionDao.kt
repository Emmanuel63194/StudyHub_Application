package edu.unicauca.aplimovil.studyhub_application.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalificacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalificacionDao {

    @Insert
    suspend fun agregar(calificacion: CalificacionEntity)

    @Update
    suspend fun editar(calificacion: CalificacionEntity)

    @Delete
    suspend fun eliminar(calificacion: CalificacionEntity)

    @Query("SELECT * FROM calificaciones ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<CalificacionEntity>>

    /** Aqui seria obtener todas las calificaciones que pertenezcan a una asignatura en especifico */
    @Query("SELECT * FROM calificaciones WHERE asignaturaId = :asignaturaId ORDER BY fecha DESC")
    fun obtenerPorAsignatura(asignaturaId: Int): Flow<List<CalificacionEntity>>

    /** Se obtiene una calificación por su id para poder editarla */
    @Query("SELECT * FROM calificaciones WHERE id = :id")
    suspend fun obtenerPorId(id: Int): CalificacionEntity?

    /** Se define que porcentaje ya ha sido utilizado por la asignatura */
    @Query("SELECT COALESCE(SUM(porcentaje), 0) FROM calificaciones WHERE asignaturaId = :asignaturaId")
    fun obtenerPorcentajeUsado(asignaturaId: Int): Flow<Int>

    /** Se utiliza para eliminar todas las calificaciones que esten relacionadas a una asignatura cuando se elimina la asignatura */
    @Query("DELETE FROM calificaciones WHERE asignaturaId = :asignaturaId")
    suspend fun eliminarPorAsignatura(asignaturaId: Int)
}