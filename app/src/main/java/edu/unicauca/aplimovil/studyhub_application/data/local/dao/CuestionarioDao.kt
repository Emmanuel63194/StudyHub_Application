package edu.unicauca.aplimovil.studyhub_application.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CuestionarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(cuestionario: CuestionarioEntity): Long

    @Update
    suspend fun actualizar(cuestionario: CuestionarioEntity)

    @Delete
    suspend fun eliminar(cuestionario: CuestionarioEntity)

    @Query("SELECT * FROM cuestionarios ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<CuestionarioEntity>>

    /** Se obtiene un cuestionario por su id para poder editarlo */
    @Query("SELECT * FROM cuestionarios WHERE id = :cuestionarioId")
    suspend fun obtenerPorId(cuestionarioId: Long): CuestionarioEntity?
}