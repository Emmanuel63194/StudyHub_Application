package edu.unicauca.aplimovil.studyhub_application.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignaturaDao {

    @Insert
    suspend fun agregar(asignatura: AsignaturaEntity)

    @Update
    suspend fun editar(asignatura: AsignaturaEntity)

    @Delete
    suspend fun eliminar(asignatura: AsignaturaEntity)

    @Query("SELECT * FROM asignaturas")
    fun obtenerTodas(): Flow<List<AsignaturaEntity>>
}

