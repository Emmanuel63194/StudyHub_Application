package edu.unicauca.aplimovil.studyhub_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unicauca.aplimovil.studyhub_application.data.local.dao.CalificacionDao
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalificacionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalificacionViewModel(
    private val dao: CalificacionDao
) : ViewModel() {

    val calificaciones = dao.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregar(asignaturaId: Int, nota: Double, porcentaje: Int, descripcion: String, fecha: Long) {
        viewModelScope.launch {
            dao.agregar(
                CalificacionEntity(
                    asignaturaId = asignaturaId,
                    nota = nota,
                    porcentaje = porcentaje,
                    descripcion = descripcion,
                    fecha = fecha
                )
            )
        }
    }

    fun editar(calificacion: CalificacionEntity) {
        viewModelScope.launch { dao.editar(calificacion) }
    }

    fun eliminar(calificacion: CalificacionEntity) {
        viewModelScope.launch { dao.eliminar(calificacion) }
    }
}