package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalificacionEntity
import edu.unicauca.aplimovil.studyhub_application.ui.components.EliminarRecurso
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoFlecha
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.viewmodel.AsignaturaViewModel
import edu.unicauca.aplimovil.studyhub_application.viewmodel.CalificacionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val DIA_MS = 24L * 60 * 60 * 1000

private val AzulPorcentajeCalificacion = Color(0xFFACC6FF)

private val FiltrosCalificaciones = listOf(
    "Hoy",
    "Ayer",
    "Última semana",
    "Último mes",
    "Todas"
)

@Composable
fun PantallaCalificaciones(
    calificacionViewModel: CalificacionViewModel,
    asignaturaViewModel: AsignaturaViewModel,
    onMenuClick: () -> Unit = {},
    onAgregarClick: () -> Unit = {},
    onEditarClick: (CalificacionEntity) -> Unit = {}
) {
    val calificaciones by calificacionViewModel.calificaciones.collectAsState()
    val asignaturas by asignaturaViewModel.asignaturas.collectAsState()

    var filtro by remember {
        mutableStateOf("Última semana")
    }

    var calificacionAEliminar by remember {
        mutableStateOf<CalificacionEntity?>(null)
    }

    val visibles = calificaciones.filter { calificacion ->
        cumpleFiltroCalificacion(calificacion.fecha, filtro) &&
                asignaturas.any { asignatura ->
                    asignatura.id == calificacion.asignaturaId
                }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            BarraSuperiorCalificaciones(
                onMenuClick = onMenuClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (visibles.isEmpty()) {
                EstadoVacioCalificaciones(
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-80).dp)
                )
            } else {
                ListaCalificaciones(
                    asignaturas = asignaturas,
                    visibles = visibles,
                    onEditar = onEditarClick,
                    onEliminar = {
                        calificacionAEliminar = it
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FilaFiltroYBoton(
            filtro = filtro,
            onFiltroChange = {
                filtro = it
            },
            onAgregarClick = onAgregarClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-36).dp)
        )
    }

    calificacionAEliminar?.let { calif ->
        EliminarRecurso(
            titulo = "¿Quieres eliminarla?",
            descripcion = "Eliminarás la calificación seleccionada",
            onCancelar = {
                calificacionAEliminar = null
            },
            onEliminar = {
                calificacionViewModel.eliminar(calif)
                calificacionAEliminar = null
            }
        )
    }
}

@Composable
private fun ListaCalificaciones(
    asignaturas: List<AsignaturaEntity>,
    visibles: List<CalificacionEntity>,
    onEditar: (CalificacionEntity) -> Unit,
    onEliminar: (CalificacionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val asignaturasConCalificaciones = asignaturas.filter { asignatura ->
        visibles.any {
            it.asignaturaId == asignatura.id
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        asignaturasConCalificaciones.forEachIndexed { indice, asignatura ->

            val grupo = visibles.filter {
                it.asignaturaId == asignatura.id
            }

            if (indice > 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(441.dp)
                                .height(1.dp)
                                .background(Color.White)
                        )
                    }
                }
            }

            item {
                Text(
                    text = asignatura.nombre,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.offset(x = 50.dp)
                )
            }

            items(
                items = grupo,
                key = { it.id }
            ) { calif ->

                TarjetaCalificacion(
                    calif = calif,
                    onEditar = {
                        onEditar(calif)
                    },
                    onEliminar = {
                        onEliminar(calif)
                    }
                )
            }
        }
    }
}

@Composable
private fun TarjetaCalificacion(
    calif: CalificacionEntity,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                MaterialTheme.colorScheme.surfaceContainer
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CirculoNota(
                nota = calif.nota
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = calif.descripcion,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatearFechaCalificacion(calif.fecha),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Image(
                    painter = painterResource(
                        id = R.drawable.editar_icono
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onEditar()
                        }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(
                        id = R.drawable.eliminar_icono
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onEliminar()
                        }
                )
            }

            Text(
                text = "${calif.porcentaje}%",
                color = AzulPorcentajeCalificacion,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun CirculoNota(
    nota: Double
) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(CircleShape)
            .background(colorDeNota(nota)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = nota.toString(),
            color = Color.White,
            fontSize = 20.sp
        )
    }
}

private fun colorDeNota(nota: Double): Color {
    return when {
        nota < 3.0 -> Color(0xFFE53935)
        nota < 4.0 -> Color(0xFFD4B106)
        else -> Color(0xFF2E9E2E)
    }
}

@Composable
private fun BarraSuperiorCalificaciones(
    onMenuClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconoHamburguesa(
            modifier = Modifier
                .size(34.dp)
                .clickable(onClick = onMenuClick)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Calificaciones",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion
        )
    }
}

@Composable
private fun EstadoVacioCalificaciones(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.buzon_vacio
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(210.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sin calificaciones",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Las calificaciones que añadas aparecerán aquí",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FilaFiltroYBoton(
    filtro: String,
    onFiltroChange: (String) -> Unit,
    onAgregarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var abierto by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (abierto) {
                            279.dp
                        } else {
                            56.dp
                        }
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer
                    )
            ) {
                if (abierto) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        FiltrosCalificaciones.forEach { opcion ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clickable {
                                        onFiltroChange(opcion)
                                        abierto = false
                                    }
                                    .padding(start = 52.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = opcion,
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable {
                            abierto = !abierto
                        }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            id = R.drawable.calendario_blanco
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 24.dp, height = 21.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = filtro,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconoFlecha(
                        modifier = Modifier
                            .size(width = 60.dp, height = 40.dp)
                            .offset(x = 15.dp)
                            .rotate(
                                if (abierto) {
                                    -90f
                                } else {
                                    90f
                                }
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        BotonAgregarCalificacion(
            onClick = onAgregarClick
        )
    }
}

@Composable
private fun BotonAgregarCalificacion(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 30.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
        )
    }
}

private fun hoyMillisCalificacion(): Long {
    val ahora = System.currentTimeMillis()
    val desfase = TimeZone.getDefault().getOffset(ahora)

    return (ahora + desfase) / DIA_MS * DIA_MS
}

private fun cumpleFiltroCalificacion(
    fecha: Long,
    filtro: String
): Boolean {
    val dias = (hoyMillisCalificacion() - fecha) / DIA_MS

    return when (filtro) {
        "Hoy" -> dias == 0L
        "Ayer" -> dias == 1L
        "Última semana" -> dias <= 6
        "Último mes" -> dias <= 29
        else -> true
    }
}

private fun formatearFechaCalificacion(
    millis: Long
): String {
    val formato = SimpleDateFormat(
        "MMMM d, yyyy",
        Locale.forLanguageTag("es-CO")
    )

    formato.timeZone = TimeZone.getTimeZone("UTC")

    return formato
        .format(Date(millis))
        .replaceFirstChar {
            it.uppercase()
        }
}
