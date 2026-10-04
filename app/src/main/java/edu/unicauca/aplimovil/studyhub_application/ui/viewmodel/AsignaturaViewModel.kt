package edu.unicauca.aplimovil.studyhub_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.unicauca.aplimovil.studyhub_application.data.local.dao.AsignaturaDao
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AsignaturaViewModel(
    private val dao: AsignaturaDao
) : ViewModel() {

    val asignaturas = dao.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregar(nombre: String, salon: String, horario: String = "") {
        viewModelScope.launch {
            dao.agregar(AsignaturaEntity(nombre = nombre, salon = salon, horario = horario))
        }
    }

    fun editar(asignatura: AsignaturaEntity) {
        viewModelScope.launch { dao.editar(asignatura) }
    }

    fun eliminar(asignatura: AsignaturaEntity) {
        viewModelScope.launch { dao.eliminar(asignatura) }
    }
}