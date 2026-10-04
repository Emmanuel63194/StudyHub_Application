package edu.unicauca.aplimovil.studyhub_application.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.unicauca.aplimovil.studyhub_application.data.local.dao.AsignaturaDao
import edu.unicauca.aplimovil.studyhub_application.data.local.dao.CalendarioDao
import edu.unicauca.aplimovil.studyhub_application.data.local.dao.CalificacionDao
import edu.unicauca.aplimovil.studyhub_application.data.local.dao.CuestionarioDao
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalificacionEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity

@Database(
    entities = [
        CuestionarioEntity::class,
        CalendarioEntity::class,
        AsignaturaEntity::class,
        CalificacionEntity::class
    ],
    version = 4,
)

@TypeConverters(Converters::class)
abstract class BaseDatosStudyHub : RoomDatabase() {

    abstract fun cuestionarioDao(): CuestionarioDao

    abstract fun calendarioDao(): CalendarioDao

    abstract fun asignaturaDao(): AsignaturaDao

    abstract fun calificacionDao(): CalificacionDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
        }
    }
}