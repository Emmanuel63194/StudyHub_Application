package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoFlecha
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import java.util.Calendar

private val DiasSemana = listOf("D", "L", "Ma", "Mi", "J", "V", "S")

private val NombresMeses = listOf(
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

private data class FechaSeleccionada(
    val anio: Int,
    val mes: Int,
    val dia: Int
)

@Composable
fun PantallaCalendario(onMenuClick: () -> Unit = {}) {

    /*
     * Obtenemos la fecha actual del dispositivo.
     *
     * Calendar usa los meses desde 0 hasta 11:
     * Enero = 0
     * Febrero = 1
     * ...
     * Septiembre = 8
     * ...
     * Diciembre = 11
     */
    val hoy = remember {
        Calendar.getInstance()
    }

    /*
     * El calendario se abre inicialmente en el mes actual.
     */
    var indiceMes by remember {
        mutableStateOf(
            (hoy.get(Calendar.YEAR) - AnioMinimo) * 12 +
                    hoy.get(Calendar.MONTH)
        )
    }

    /*
     * El día seleccionado comienza siendo el día de hoy.
     *
     * Por ejemplo:
     * 28 de septiembre de 2026
     */
    var fechaSeleccionada by remember {
        mutableStateOf(
            FechaSeleccionada(
                anio = hoy.get(Calendar.YEAR),
                mes = hoy.get(Calendar.MONTH),
                dia = hoy.get(Calendar.DAY_OF_MONTH)
            )
        )
    }

    /*
     * Convertimos el índice en año y mes.
     */
    val anioActual = AnioMinimo + indiceMes / 12
    val mesActual = indiceMes % 12

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
            puedeRetroceder = indiceMes > 0,
            puedeAvanzar = indiceMes < ((AnioMaximo - AnioMinimo + 1) * 12 - 1),
            onMesAnterior = {
                if (indiceMes > 0) {
                    indiceMes--
                }
            },
            onMesSiguiente = {
                if (indiceMes < ((AnioMaximo - AnioMinimo + 1) * 12 - 1)) {
                    indiceMes++
                }
            },
            onDiaSeleccionado = { dia ->
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

            Spacer(modifier = Modifier.height(48.dp))

            EstadoVacioEventos()

            Spacer(modifier = Modifier.weight(1f))

            SeccionAccionCalendario()
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
    puedeRetroceder: Boolean,
    puedeAvanzar: Boolean,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onDiaSeleccionado: (Int) -> Unit
) {

    val semanas = obtenerSemanasDelMes(
        anio = anio,
        mes = mes
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {

        NavegacionMes(
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

        semanas.forEach { semana ->
            FilaDeDias(
                semana = semana,
                anio = anio,
                mes = mes,
                fechaSeleccionada = fechaSeleccionada,
                onDiaSeleccionado = onDiaSeleccionado
            )
        }
    }
}

@Composable
private fun NavegacionMes(
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

        Text(
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
private fun EncabezadoDiasSemana() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        DiasSemana.forEach { dia ->

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
private fun FilaDeDias(
    semana: List<Int?>,
    anio: Int,
    mes: Int,
    fechaSeleccionada: FechaSeleccionada,
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

                if (dia != null) {

                    CeldaDia(
                        dia = dia,
                        estaSeleccionado =
                            fechaSeleccionada.anio == anio &&
                                    fechaSeleccionada.mes == mes &&
                                    fechaSeleccionada.dia == dia,
                        onClick = {
                            onDiaSeleccionado(dia)
                        }
                    )
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
                    androidx.compose.ui.graphics.Color.Transparent
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

    val calendario = Calendar.getInstance().apply {

        set(Calendar.YEAR, anio)
        set(Calendar.MONTH, mes)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    /*
     * Calendar.DAY_OF_WEEK:
     *
     * Domingo = 1
     * Lunes = 2
     * Martes = 3
     * Miércoles = 4
     * Jueves = 5
     * Viernes = 6
     * Sábado = 7
     *
     * Como nuestro calendario empieza en domingo,
     * restamos Calendar.SUNDAY para obtener:
     *
     * Domingo = 0
     * Lunes = 1
     * ...
     * Sábado = 6
     */
    val posicionPrimerDia =
        calendario.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

    val cantidadDias =
        calendario.getActualMaximum(Calendar.DAY_OF_MONTH)

    val semanas = mutableListOf<List<Int?>>()

    var semanaActual = mutableListOf<Int?>()

    /*
     * Espacios antes del primer día del mes.
     */
    repeat(posicionPrimerDia) {
        semanaActual.add(null)
    }

    /*
     * Agregamos todos los días del mes.
     */
    for (dia in 1..cantidadDias) {

        semanaActual.add(dia)

        if (semanaActual.size == 7) {

            semanas.add(semanaActual)

            semanaActual = mutableListOf()
        }
    }

    /*
     * Completamos la última semana con espacios
     * hasta llegar a 7 columnas.
     */
    if (semanaActual.isNotEmpty()) {

        while (semanaActual.size < 7) {
            semanaActual.add(null)
        }

        semanas.add(semanaActual)
    }

    return semanas
}

@Composable
private fun EstadoVacioEventos() {

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
private fun SeccionAccionCalendario() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-55).dp),
        horizontalArrangement = Arrangement.End
    ) {

        BotonAgregarCalendario()
    }
}

@Composable
private fun BotonAgregarCalendario() {

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary),
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

