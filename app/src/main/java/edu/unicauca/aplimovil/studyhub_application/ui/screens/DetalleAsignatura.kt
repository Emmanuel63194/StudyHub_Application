package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalificacionEntity
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.viewmodel.CalificacionViewModel
import java.util.Calendar

@Composable
fun DetalleAsignatura(
    asignatura: AsignaturaEntity?,
    calificacionViewModel: CalificacionViewModel,
    onBack: () -> Unit = {}
) {
    val todas by calificacionViewModel.calificaciones.collectAsState()
    var mostrarAyuda by remember { mutableStateOf(false) }

    if (asignatura == null) return

    val notas = todas.filter {
        it.asignaturaId == asignatura.id
    }

    val calificacionesRecientes = notas.filter { calificacion ->
        val fecha = calificacion.fecha

        if (fecha !is Long) {
            false
        } else {
            val inicioHoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val inicioHaceCincoDias =
                inicioHoy.clone() as Calendar

            inicioHaceCincoDias.add(
                Calendar.DAY_OF_YEAR,
                -5
            )

            val inicioManana =
                inicioHoy.clone() as Calendar

            inicioManana.add(
                Calendar.DAY_OF_YEAR,
                1
            )

            fecha >= inicioHaceCincoDias.timeInMillis &&
                    fecha < inicioManana.timeInMillis
        }
    }

    val porcentajeTotal = notas.sumOf {
        it.porcentaje
    }

    val promedio =
        if (porcentajeTotal == 0) {
            null
        } else {
            notas.sumOf {
                it.nota * it.porcentaje
            } / porcentajeTotal
        }

    val bloques = textoABloques(asignatura.horario)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.regresar_icono
                ),
                contentDescription = "Regresar",
                modifier = Modifier
                    .size(34.dp)
                    .clickable {
                        onBack()
                    }
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Text(
                text = asignatura.nombre,
                color = MaterialTheme.colorScheme.onBackground,
                style = TipografiaStudyHub.TituloSeccion,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        if (asignatura.salon.isNotEmpty()) {
            TarjetaDetalle(
                alturaMinima = 69.dp,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.ubicacion_blanco_icono
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = asignatura.salon,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        TarjetaDetalle(
            alturaMinima = 134.dp
        ) {
            TituloTarjeta(
                R.drawable.promedio_icono,
                "Promedio"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp),
                contentAlignment = Alignment.Center
            ) {
                if (promedio == null) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = 25.dp,
                                height = 4.dp
                            )
                            .background(Color.White)
                    )
                } else {
                    Text(
                        text = String.format(
                            "%.1f",
                            promedio
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (bloques.isNotEmpty()) {
            TarjetaDetalle(
                alturaMinima = 120.dp
            ) {
                TituloTarjeta(
                    R.drawable.horario_blanco_icono,
                    "Horario semanal"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                TablaHorario(bloques)
            }
        }

        TarjetaDetalle(
            alturaMinima =
                if (calificacionesRecientes.isEmpty()) {
                    160.dp
                } else {
                    120.dp
                }
        ) {
            TituloTarjeta(
                R.drawable.calificacion_icono,
                "Calificaciones recientes"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (calificacionesRecientes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin calificaciones",
                        color = ColorTextoSecundarioEvento,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                calificacionesRecientes
                    .take(3)
                    .forEach { calif ->
                        FilaCalificacionReciente(calif)
                    }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .background(
                    MaterialTheme.colorScheme.surfaceContainer
                )
                .clickable {
                    mostrarAyuda = true
                }
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {
            Text(
                text = "¿Cómo funciona el promedio?",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp
            )
        }
    }

    if (mostrarAyuda) {
        DialogoPromedio(
            onCerrar = {
                mostrarAyuda = false
            }
        )
    }
}

@Composable
private fun TarjetaDetalle(
    alturaMinima: Dp = 0.dp,
    verticalArrangement: Arrangement.Vertical =
        Arrangement.Top,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = alturaMinima)
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(
                MaterialTheme.colorScheme.surfaceContainer
            )
            .padding(16.dp),
        verticalArrangement = verticalArrangement,
        content = contenido
    )
}

@Composable
private fun TituloTarjeta(
    icono: Int,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icono),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = texto,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )
    }
}

@Composable
private fun ColumnScope.TablaHorario(
    bloques: List<BloqueDia>
) {
    Column(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .widthIn(max = 340.dp)
            .fillMaxWidth()
            .border(
                1.dp,
                Color.White
            )
    ) {
        bloques.forEachIndexed { indice, bloque ->

            if (indice > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = bloque.dia,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(Color.White)
                )

                Text(
                    text =
                        "${formatearHoraEvento(bloque.inicio)} - " +
                                formatearHoraEvento(bloque.fin),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun FilaCalificacionReciente(
    calif: CalificacionEntity
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(
                    colorCirculoCalificacion(calif.nota)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(
                    "%.1f",
                    calif.nota
                ),
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = calif.descripcion,
            color = ColorTextoSecundarioEvento,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        val fecha: Any = calif.fecha

        Text(
            text = if (fecha is Long) {
                formatearFechaEvento(fecha)
            } else {
                fecha.toString()
            },
            color = ColorTextoSecundarioEvento,
            fontSize = 15.sp
        )
    }
}

private fun colorCirculoCalificacion(
    nota: Double
): Color {
    return when {
        nota < 3.0 -> Color(0xFFFF9800)
        nota < 4.0 -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }
}

@Composable
private fun DialogoPromedio(
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor =
            MaterialTheme.colorScheme.surfaceContainer,
        title = {
            Text(
                "¿Cómo funciona el promedio?",
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        text = {
            Text(
                text =
                    "Es un promedio ponderado: cada nota vale según su porcentaje.\n\n" +
                            "Promedio = (nota × %) de todas las notas, dividido entre la suma de los porcentajes.\n\n" +
                            "Ejemplo: 4.0 (40%) y 3.0 (60%) → " +
                            "(4.0×40 + 3.0×60) ÷ 100 = 3.4\n\n" +
                            "Si aún no has registrado el 100%, se calcula solo con lo que ya tienes.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onCerrar,
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    "Entendido",
                    fontSize = 18.sp
                )
            }
        }
    )
}

