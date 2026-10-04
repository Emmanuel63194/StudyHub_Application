package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.components.ResumenEmergente
import edu.unicauca.aplimovil.studyhub_application.ui.components.TarjetaResumen
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import java.util.Calendar

@Composable
fun PantallaInicio(
    onMenuClick: () -> Unit = {},
    eventos: List<CalendarioEntity> = emptyList(),
    eventosCompletados: Set<Long> = emptySet(),
    onAlternarCompletado: (Long) -> Unit = {},
    onEliminarEvento: (CalendarioEntity) -> Unit = {}
) {

    val hoyMillis = inicioDelDiaMillis(diasDesdeHoy = 0)
    val mananaMillis = inicioDelDiaMillis(diasDesdeHoy = 1)

    val eventosHoy = remember(eventos, hoyMillis) {
        eventos.filter { evento -> evento.fecha == hoyMillis }
    }
    val eventosManana = remember(eventos, mananaMillis) {
        eventos.filter { evento -> evento.fecha == mananaMillis }
    }

    var mostrarDetalle by remember { mutableStateOf(false) }
    var eventoDetalle by remember { mutableStateOf<CalendarioEntity?>(null) }

    val abrirDetalle: (CalendarioEntity) -> Unit = { evento ->
        eventoDetalle = evento
        mostrarDetalle = true
    }

    BackHandler(enabled = mostrarDetalle) {
        mostrarDetalle = false
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoHamburguesa(
                    modifier = Modifier
                        .size(width = 34.dp, height = 34.dp)
                        .clickable(onClick = onMenuClick)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Resumen",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TipografiaStudyHub.TituloSeccion
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SeccionEventosDelDia(
                titulo = "Hoy",
                eventos = eventosHoy,
                eventosCompletados = eventosCompletados,
                onAlternarCompletado = onAlternarCompletado,
                onAbrirDetalle = abrirDetalle,
                onEliminarEvento = onEliminarEvento
            )

            Spacer(modifier = Modifier.height(28.dp))

            SeccionEventosDelDia(
                titulo = "Mañana",
                eventos = eventosManana,
                eventosCompletados = eventosCompletados,
                onAlternarCompletado = onAlternarCompletado,
                onAbrirDetalle = abrirDetalle,
                onEliminarEvento = onEliminarEvento
            )
        }

        AnimatedVisibility(
            visible = mostrarDetalle,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { mostrarDetalle = false }
                    )
            )
        }

        AnimatedVisibility(
            visible = mostrarDetalle,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(
                animationSpec = tween(300),
                initialOffsetY = { alto -> alto }
            ),
            exit = slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { alto -> alto }
            )
        ) {
            eventoDetalle?.let { evento ->
                ResumenEmergente(
                    evento = evento,
                    onCerrar = { mostrarDetalle = false }
                )
            }
        }
    }
}

private fun inicioDelDiaMillis(diasDesdeHoy: Int): Long {
    val calendario = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, diasDesdeHoy)
    }
    return fechaAMillis(
        anio = calendario.get(Calendar.YEAR),
        mes = calendario.get(Calendar.MONTH),
        dia = calendario.get(Calendar.DAY_OF_MONTH)
    )
}

@Composable
private fun SeccionEventosDelDia(
    titulo: String,
    eventos: List<CalendarioEntity>,
    eventosCompletados: Set<Long>,
    onAlternarCompletado: (Long) -> Unit,
    onAbrirDetalle: (CalendarioEntity) -> Unit,
    onEliminarEvento: (CalendarioEntity) -> Unit
) {

    Text(
        text = titulo,
        color = MaterialTheme.colorScheme.onBackground,
        style = TipografiaStudyHub.ResaltadoTarjeta
    )

    Spacer(modifier = Modifier.height(12.dp))

    if (eventos.isEmpty()) {

        TarjetaResumen()

    } else {

        TarjetaResumen(
            contenidoEventos = {

                eventos.forEachIndexed { indice, evento ->

                    key(evento.id) {

                        if (indice > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        FilaEventoResumen(
                            evento = evento,
                            completado = evento.id in eventosCompletados,
                            onAlternarCompletado = { onAlternarCompletado(evento.id) },
                            onAbrirDetalle = { onAbrirDetalle(evento) },
                            onEliminar = { onEliminarEvento(evento) }
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun FilaEventoResumen(
    evento: CalendarioEntity,
    completado: Boolean,
    onAlternarCompletado: () -> Unit,
    onAbrirDetalle: () -> Unit,
    onEliminar: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(30.dp)
                .border(width = 2.dp, color = Color.White, shape = CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onAlternarCompletado),
            contentAlignment = Alignment.Center
        ) {
            if (completado) {
                Image(
                    painter = painterResource(id = R.drawable.check_icono),
                    contentDescription = "Completado",
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onAbrirDetalle)
        ) {

            Text(
                text = evento.titulo,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (completado) TextDecoration.LineThrough else null,
                modifier = Modifier.padding(top = 3.dp)
            )

            if (evento.nota.isNotBlank()) {
                Text(
                    text = evento.nota,
                    color = ColorTextoSecundarioEvento,
                    fontSize = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (completado) {

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.eliminar_icono),
                contentDescription = "Eliminar evento",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = onEliminar)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111318)
@Composable
fun PantallaInicioPreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        PantallaInicio()
    }
}