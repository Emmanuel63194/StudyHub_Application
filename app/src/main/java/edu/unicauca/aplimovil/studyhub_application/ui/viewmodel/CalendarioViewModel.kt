package edu.unicauca.aplimovil.studyhub_application.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.unicauca.aplimovil.studyhub_application.data.local.BaseDatosStudyHub
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import edu.unicauca.aplimovil.studyhub_application.notificaciones.ProgramadorNotificacionesEvento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val calendarioDao =
        BaseDatosStudyHub.obtenerInstancia(application).calendarioDao()

    /** Todos los eventos ordenados por fecha y hora */
    val eventos: StateFlow<List<CalendarioEntity>> =
        calendarioDao.obtenerTodos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _eventosCompletados = MutableStateFlow<Set<Long>>(emptySet()) // guarda los identificadores de los eventos que fueron completados
    val eventosCompletados: StateFlow<Set<Long>> = _eventosCompletados.asStateFlow() // expone los eventos completados sin permitir modificaciones externas

    fun alternarCompletado(eventoId: Long) { // marca un evento como completado o lo desmarca si ya estaba completado
        _eventosCompletados.update { completados ->
            if (eventoId in completados) completados - eventoId else completados + eventoId
        }
    }

    fun guardarEvento(evento: CalendarioEntity) { // guarda un evento ya sea nuevo o actualizado
        viewModelScope.launch {
            val idGuardado = if (evento.id == 0L) {
                calendarioDao.insertar(evento)
            } else {
                calendarioDao.actualizar(evento)
                evento.id
            }

            ProgramadorNotificacionesEvento.programar( // programa la notificacion
                contexto = getApplication(),
                evento = evento.copy(id = idGuardado)
            )
        }
    }

    /** Elimina el evento y cancela su notificación. */
    fun eliminarEvento(evento: CalendarioEntity) {
        viewModelScope.launch {
            calendarioDao.eliminar(evento)

            _eventosCompletados.update { completados -> completados - evento.id }

            ProgramadorNotificacionesEvento.cancelar(
                contexto = getApplication(),
                eventoId = evento.id
            )
        }
    }
}