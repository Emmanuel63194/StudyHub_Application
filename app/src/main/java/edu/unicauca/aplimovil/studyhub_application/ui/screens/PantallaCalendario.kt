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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import edu.unicauca.aplimovil.studyhub_application.ui.components.EliminarRecurso
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoFlecha
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import java.util.Calendar

private val DiasSemana = listOf("D", "L", "Ma", "Mi", "J", "V", "S")

internal val NombresMeses = listOf(
    "Enero",
    "Febrero",
    "Marzo",
    "Abril",
    "Mayo",
    "Junio",
    "Julio",
    "Agosto",
    "Septiembre",
    "Octubre",
    "Noviembre",
    "Diciembre"
)

private const val AnioMinimo = 2000
private const val AnioMaximo = 2200

private val TamanoIndicadorEvento = 10.dp // define el tamaño del indicados donde aparece los dias con eventos

private data class FechaSeleccionada( // representa uan fecha seleccionada
    val anio: Int,
    val mes: Int,
    val dia: Int
)

@Composable
fun PantallaCalendario(
    onMenuClick: () -> Unit = {},
    eventos: List<CalendarioEntity> = emptyList(),
    onGuardarEvento: (CalendarioEntity) -> Unit = {},
    onEliminarEvento: (CalendarioEntity) -> Unit = {}
) {

    val hoy = remember { // obtiene la fecha actual para utilizarala como punto inicial del calendario
        Calendar.getInstance()
    }

    var indiceMes by remember { // calcula la posicion del mes actual dentro del rango permitido
        mutableStateOf(
            (hoy.get(Calendar.YEAR) - AnioMinimo) * 12 +
                    hoy.get(Calendar.MONTH)
        )
    }

    var fechaSeleccionada by remember { // guarda el dia que actualmente esta selecionado en el calendario
        mutableStateOf(
            FechaSeleccionada(
                anio = hoy.get(Calendar.YEAR),
                mes = hoy.get(Calendar.MONTH),
                dia = hoy.get(Calendar.DAY_OF_MONTH)
            )
        )
    }

    var mostrarNuevoEvento by remember { mutableStateOf(false) } // controla si se muestra el formulario paracrear o editar un evento
    var eventoEnEdicion by remember { mutableStateOf<CalendarioEntity?>(null) } // guarda el evento que se esta editando

    var eventoPorEliminar by remember { mutableStateOf<CalendarioEntity?>(null) } // guarda temporalmente el evento que espera confirmacion para ser eliminado

    val diasConEventos = remember(eventos) { // obtiene las fechas que tiene nal menos un evento para mostrar su indicador
        eventos.map { it.fecha }.toSet()
    }

    val eventosDelDia = remember(eventos, fechaSeleccionada) { // filtra los eventos correspoodiente al dia actualemente seleccionado
        val fechaElegida = fechaAMillis(
            fechaSeleccionada.anio,
            fechaSeleccionada.mes,
            fechaSeleccionada.dia
        )
        eventos.filter { evento -> evento.fecha == fechaElegida }
    }

    val anioActual = AnioMinimo + indiceMes / 12 // obtiene año y mes que corresponden al indice actual
    val mesActual = indiceMes % 12

    BackHandler(enabled = mostrarNuevoEvento) {
        mostrarNuevoEvento = false
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                BarraSuperiorCalendario(
                    onMenuClick = onMenuClick
                )
            }

            ContenedorCalendario(
                anio = anioActual,
                mes = mesActual,
                fechaSeleccionada = fechaSeleccionada,
                diasConEventos = diasConEventos,
                puedeRetroceder = indiceMes > 0,
                puedeAvanzar = indiceMes < ((AnioMaximo - AnioMinimo + 1) * 12 - 1),
                onMesAnterior = { // cambia al mes anterior cuando todavia esta dentro del rango permitido
                    if (indiceMes > 0) {
                        indiceMes--
                    }
                },
                onMesSiguiente = { // cambia al mes siguiente cuando todavia esta dentro del rango permitido
                    if (indiceMes < ((AnioMaximo - AnioMinimo + 1) * 12 - 1)) {
                        indiceMes++
                    }
                },
                onDiaSeleccionado = { dia -> // actualiar el dia seleccionado del mes mostrado
                    fechaSeleccionada = FechaSeleccionada(
                        anio = anioActual,
                        mes = mesActual,
                        dia = dia
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {

                if (eventosDelDia.isEmpty()) {

                    Spacer(modifier = Modifier.height(48.dp))

                    EstadoVacioEventos()

                    Spacer(modifier = Modifier.weight(1f))

                } else {

                    ListaEventos(
                        eventos = eventosDelDia,
                        onEditar = { evento ->
                            eventoEnEdicion = evento
                            mostrarNuevoEvento = true
                        },
                        onEliminar = { evento ->
                            // Primero se pide confirmación.
                            eventoPorEliminar = evento
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                SeccionAccionCalendario(
                    onAgregarClick = {
                        eventoEnEdicion = null
                        mostrarNuevoEvento = true
                    }
                )
            }
        }

        eventoPorEliminar?.let { evento -> // mostrar dialogo de confirmacion para eliminar
            EliminarRecurso(
                titulo = "¿Quieres eliminarlo?",
                descripcion = "Eliminarás el evento seleccionado.",
                onCancelar = { eventoPorEliminar = null },
                onEliminar = {
                    onEliminarEvento(evento)
                    eventoPorEliminar = null
                }
            )
        }

        AnimatedVisibility(
            visible = mostrarNuevoEvento,
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
                        onClick = { mostrarNuevoEvento = false }
                    )
            )
        }

        AnimatedVisibility( // mostrar el formulario del evento cuando entra desde la parte inferior de la pantalla
            visible = mostrarNuevoEvento,
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
            NuevoEvento(
                eventoAEditar = eventoEnEdicion,
                onCerrar = { mostrarNuevoEvento = false },
                onGuardar = onGuardarEvento
            )
        }
    }
}

@Composable
private fun BarraSuperiorCalendario(
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconoHamburguesa(
            modifier = Modifier
                .size(width = 34.dp, height = 34.dp)
                .clickable(onClick = onMenuClick)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Calendario",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion
        )
    }
}

@Composable
private fun ContenedorCalendario(
    anio: Int,
    mes: Int,
    fechaSeleccionada: FechaSeleccionada,
    diasConEventos: Set<Long>,
    puedeRetroceder: Boolean,
    puedeAvanzar: Boolean,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onDiaSeleccionado: (Int) -> Unit
) {

    val semanas = obtenerSemanasDelMes( // obtiene las semanas
        anio = anio,
        mes = mes
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {

        NavegacionMes( // muestra el nombre del mes, año y botones para cambiar de mes
            anio = anio,
            mes = mes,
            puedeRetroceder = puedeRetroceder,
            puedeAvanzar = puedeAvanzar,
            onMesAnterior = onMesAnterior,
            onMesSiguiente = onMesSiguiente
        )

        Spacer(modifier = Modifier.height(16.dp))

        EncabezadoDiasSemana()

        Spacer(modifier = Modifier.height(8.dp))

        semanas.forEach { semana -> // crea una fila visual por cada semana del mes
            FilaDeDias(
                semana = semana,
                anio = anio,
                mes = mes,
                fechaSeleccionada = fechaSeleccionada,
                diasConEventos = diasConEventos,
                onDiaSeleccionado = onDiaSeleccionado
            )
        }
    }
}

@Composable
private fun NavegacionMes( // crear la parte superior del calendario donde se puede cambiar de mes
    anio: Int,
    mes: Int,
    puedeRetroceder: Boolean,
    puedeAvanzar: Boolean,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconoFlecha(
            modifier = Modifier
                .size(width = 60.dp, height = 40.dp)
                .offset(x = -3.dp)
                .rotate(180f)
                .clickable(
                    enabled = puedeRetroceder,
                    onClick = onMesAnterior
                )
        )

        Text( // muestra el nombre y el año del mes actual
            text = "${NombresMeses[mes]} $anio",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion
        )

        IconoFlecha(
            modifier = Modifier
                .size(width = 60.dp, height = 40.dp)
                .offset(x = 3.dp)
                .clickable(
                    enabled = puedeAvanzar,
                    onClick = onMesSiguiente
                )
        )
    }
}

@Composable
private fun EncabezadoDiasSemana() { // muestra todos los nombres de los dias que aparecen en la parte superior del calendario
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        DiasSemana.forEach { dia -> // recorre los dias y colocacada uno en una columna de igual tamaño

            Text(
                text = dia,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilaDeDias( // construir una fila del calendario con sus siente posiciones
    semana: List<Int?>,
    anio: Int,
    mes: Int,
    fechaSeleccionada: FechaSeleccionada,
    diasConEventos: Set<Long>,
    onDiaSeleccionado: (Int) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        semana.forEach { dia ->

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                if (dia != null) { // solo crea una celda cuando existe un dia en esa posicion

                    CeldaDia( // muestra el numero del dia y permite seleccionarlo
                        dia = dia,
                        estaSeleccionado =
                            fechaSeleccionada.anio == anio &&
                                    fechaSeleccionada.mes == mes &&
                                    fechaSeleccionada.dia == dia,
                        onClick = {
                            onDiaSeleccionado(dia)
                        }
                    )

                    if (fechaAMillis(anio, mes, dia) in diasConEventos) { // muestra un indicador rojo debajo del dia si tiene evento
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .size(TamanoIndicadorEvento)
                                .background(ColorRojoEvento)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CeldaDia(
    dia: Int,
    estaSeleccionado: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(
                if (estaSeleccionado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = dia.toString(),
            color = if (estaSeleccionado) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            fontSize = 20.sp
        )
    }
}

private fun obtenerSemanasDelMes(
    anio: Int,
    mes: Int
): List<List<Int?>> {

    val calendario = Calendar.getInstance().apply { // configura un objeto calendar para comenzar desde el primer dia del mes

        set(Calendar.YEAR, anio)
        set(Calendar.MONTH, mes)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val posicionPrimerDia = // obtiene la posicion del primer dia tomando el domingo como primera columna
        calendario.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

    val cantidadDias =  // Obtiene la cantidad total de días que tiene el mes.
        calendario.getActualMaximum(Calendar.DAY_OF_MONTH)

    val semanas = mutableListOf<List<Int?>>() // Crea la lista donde se almacenarán todas las semanas.

    var semanaActual = mutableListOf<Int?>() // Crea temporalmente la semana que se está construyendo.

    repeat(posicionPrimerDia) { // Agrega espacios vacíos antes del primer día del mes.
        semanaActual.add(null)
    }

    for (dia in 1..cantidadDias) { // Agrega cada día del mes a la semana correspondiente.

        semanaActual.add(dia)

        if (semanaActual.size == 7) {  // Cuando la semana tiene siete posiciones, se guarda y comienza otra.

            semanas.add(semanaActual)

            semanaActual = mutableListOf()
        }
    }

    if (semanaActual.isNotEmpty()) {  // Completa y guarda la última semana si todavía tiene días pendientes.

        while (semanaActual.size < 7) {
            semanaActual.add(null)
        }

        semanas.add(semanaActual)
    }

    return semanas
}

@Composable
private fun EstadoVacioEventos() { // Muestra el mensaje y la imagen cuando el día seleccionado no tiene eventos.

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 55.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.calendario_color
            ),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Sin eventos",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ListaEventos(
    eventos: List<CalendarioEntity>,
    onEditar: (CalendarioEntity) -> Unit,
    onEliminar: (CalendarioEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn( // Crea una lista desplazable para mostrar todos los eventos.
        modifier = modifier.fillMaxWidth(),

        contentPadding = PaddingValues(bottom = 90.dp)
    ) {

        item {
            Spacer(modifier = Modifier.height(20.dp))
            LineaDivisoriaEvento()
        }

        items( // Recorre los eventos y crea un elemento visual para cada uno.
            items = eventos,
            key = { evento -> evento.id }
        ) { evento ->
            ItemEvento(
                evento = evento,
                onEditar = { onEditar(evento) },
                onEliminar = { onEliminar(evento) }
            )
        }
    }
}

@Composable
private fun ItemEvento( // Muestra la información y las acciones de un evento individual
    evento: CalendarioEntity,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = formatearFechaHoraEvento(evento),
                color = ColorTextoSecundarioEvento,
                fontSize = 15.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.editar_icono),
                contentDescription = "Editar evento",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = onEditar)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.eliminar_icono),
                contentDescription = "Eliminar evento",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = onEliminar)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        val tieneNota = evento.nota.isNotBlank() // Comprueba si el evento tiene una nota para ajustar el espacio inferior del título.

        Text(
            text = evento.titulo,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()

                .padding(top = 6.dp, bottom = if (tieneNota) 0.dp else 12.dp)
        )

        if (tieneNota) { // Muestra la nota únicamente cuando el evento tiene contenido en ella.

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = evento.nota,
                color = ColorTextoSecundarioEvento,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp)
            )
        }

        LineaDivisoriaEvento()
    }
}

@Composable
private fun SeccionAccionCalendario(
    onAgregarClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-55).dp),
        horizontalArrangement = Arrangement.End
    ) {

        BotonAgregarCalendario(onClick = onAgregarClick)
    }
}

@Composable
private fun BotonAgregarCalendario(
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "+",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF111318
)
@Composable
fun PantallaCalendarioPreview() {

    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        PantallaCalendario()
    }
}