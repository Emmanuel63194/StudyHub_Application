package edu.unicauca.aplimovil.studyhub_application.notificaciones

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import java.util.Calendar

object ProgramadorNotificacionesEvento {

    /** Variable para organizar las notificaciones, para decirle a android a que grupo pertenecen*/
    private const val CANAL_ID = "eventos_calendario"

    /** Variable para definir que accion debe suceder cuando suene la alarma*/
    private const val ACCION_NOTIFICAR =
        "edu.unicauca.aplimovil.studyhub_application.NOTIFICAR_EVENTO"

    const val EXTRA_ID = "evento_id"
    const val EXTRA_TITULO = "evento_titulo"
    const val EXTRA_NOTA = "evento_nota"

    /** Funcion para obtener el momento exacto para ejecutar la notificacion */
    fun momentoDelEvento(evento: CalendarioEntity): Long =
        Calendar.getInstance().apply {
            timeInMillis = evento.fecha
            set(Calendar.HOUR_OF_DAY, evento.hora / 60)
            set(Calendar.MINUTE, evento.hora % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    /** Funcion para programar la notificacion */
    fun programar(contexto: Context, evento: CalendarioEntity) {
        cancelarAlarma(contexto, evento.id)

        val momento = momentoDelEvento(evento)
        if (momento <= System.currentTimeMillis()) return

        crearCanal(contexto)

        val alarmManager = contexto.getSystemService(AlarmManager::class.java)

        val intent = Intent(contexto, ReceptorNotificacionEvento::class.java).apply {
            action = ACCION_NOTIFICAR
            putExtra(EXTRA_ID, evento.id)
            putExtra(EXTRA_TITULO, evento.titulo)
            putExtra(EXTRA_NOTA, evento.nota)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            contexto,
            evento.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val puedeExacta = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()

        try {
            if (puedeExacta) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, momento, pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, momento, pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, momento, pendingIntent
            )
        }
    }

    /** Cancela la alarma pendiente en caso que se elimine un evento del calendario antes de haber ocurrido */
    fun cancelar(contexto: Context, eventoId: Long) {
        cancelarAlarma(contexto, eventoId)
        NotificationManagerCompat.from(contexto).cancel(eventoId.toInt())
    }

    /** Busca la alarma para poder cancelarla */
    private fun cancelarAlarma(contexto: Context, eventoId: Long) {
        val intent = Intent(contexto, ReceptorNotificacionEvento::class.java).apply {
            action = ACCION_NOTIFICAR
        }

        val pendingIntent = PendingIntent.getBroadcast(
            contexto,
            eventoId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            contexto.getSystemService(AlarmManager::class.java).cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /** Muestra la notificación en el panel de Android. */
    fun mostrarNotificacion(
        contexto: Context,
        eventoId: Long,
        titulo: String,
        nota: String
    ) {
        crearCanal(contexto)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                contexto, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Cuando se toque la notificacion entonces deberia abrir la aplicacion en la seccion de calendario
        val abrirApp = contexto.packageManager
            .getLaunchIntentForPackage(contexto.packageName)
            ?.let { intentApp ->
                PendingIntent.getActivity(
                    contexto,
                    eventoId.toInt(),
                    intentApp,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notificacion = NotificationCompat.Builder(contexto, CANAL_ID)
            .setSmallIcon(R.drawable.calendario_icono)
            .setContentTitle(titulo)
            .setContentText(nota)
            .setStyle(NotificationCompat.BigTextStyle().bigText(nota))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)
            .setContentIntent(abrirApp)
            .build()

        try {
            NotificationManagerCompat.from(contexto)
                .notify(eventoId.toInt(), notificacion)
        } catch (e: SecurityException) {
        }
    }

    /** Funcion que define el canal para mostrar la notificacion */
    private fun crearCanal(contexto: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val canal = NotificationChannel(
            CANAL_ID,
            "Eventos del calendario",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones del calendario"
        }

        contexto.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(canal)
    }
}