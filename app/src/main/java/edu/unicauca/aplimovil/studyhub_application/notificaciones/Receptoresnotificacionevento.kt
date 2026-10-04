package edu.unicauca.aplimovil.studyhub_application.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import edu.unicauca.aplimovil.studyhub_application.data.local.BaseDatosStudyHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Se define que debe hacer android cuando llega una alarma */
class ReceptorNotificacionEvento : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val eventoId = intent.getLongExtra(ProgramadorNotificacionesEvento.EXTRA_ID, -1L)
        if (eventoId == -1L) return

        ProgramadorNotificacionesEvento.mostrarNotificacion(
            contexto = context,
            eventoId = eventoId,
            titulo = intent.getStringExtra(ProgramadorNotificacionesEvento.EXTRA_TITULO) ?: "",
            nota = intent.getStringExtra(ProgramadorNotificacionesEvento.EXTRA_NOTA) ?: ""
        )
    }
}

/** Permite recuperar las alarmas despues de que el celular se reinicia */
class ReceptorReinicioDispositivo : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendiente = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                BaseDatosStudyHub.obtenerInstancia(context)
                    .calendarioDao()
                    .obtenerTodosUnaVez()
                    .forEach { evento ->
                        ProgramadorNotificacionesEvento.programar(context, evento)
                    }
            } finally {
                pendiente.finish()
            }
        }
    }
}