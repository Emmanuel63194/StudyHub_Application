package edu.unicauca.aplimovil.studyhub_application.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.unicauca.aplimovil.studyhub_application.data.local.dao.CuestionarioDao
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity

/**
 * Base de datos Room ÚNICA y general de StudyHub.
 *
 * Por ahora solo registra [CuestionarioEntity]. Las demás entidades
 * (asignaturas, calificaciones, eventos) se agregarán aquí mismo.
 */
@Database(
    entities = [CuestionarioEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BaseDatosStudyHub : RoomDatabase() {

    abstract fun cuestionarioDao(): CuestionarioDao

    companion object {
        private const val NOMBRE_BASE_DATOS = "studyhub_database"

        @Volatile
        private var instancia: BaseDatosStudyHub? = null

        fun obtenerInstancia(context: Context): BaseDatosStudyHub {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosStudyHub::class.java,
                    NOMBRE_BASE_DATOS
                )
                    // La estructura cambió respecto a la versión 1: se recrea la base.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
        }
    }
}