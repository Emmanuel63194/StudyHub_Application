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

// Nombres de los días de la semana (encabezado del calendario).
private val DiasSemana = listOf("D", "L", "Ma", "Mi", "J", "V", "S")

// Cuadrícula de referencia para agosto de 2026 (agosto 1 cae en sábado).
// "null" representa una celda vacía dentro de la cuadrícula de 7 columnas.
private val DiasAgosto2026: List<List<Int?>> = listOf(
    listOf(null, null, null, null, null, null, 1),
    listOf(2, 3, 4, 5, 6, 7, 8),
    listOf(9, 10, 11, 12, 13, 14, 15),
    listOf(16, 17, 18, 19, 20, 21, 22),
    listOf(23, 24, 25, 26, 27, 28, 29),
    listOf(30, 31, null, null, null, null, null)
)

private const val DiaSeleccionado = 12

/**
 * Pantalla de Calendario de StudyHub.
 *
 * [onMenuClick] se invoca al pulsar el ícono de hamburguesa y permite que
 * quien aloje esta pantalla (MainActivity) abra el Navigation Drawer
 * centralizado de la aplicación.
 */
@Composable
fun PantallaCalendario(onMenuClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {

        // Todo lo demás conserva los 20 dp
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            BarraSuperiorCalendario(onMenuClick = onMenuClick)

            Spacer(modifier = Modifier.height(20.dp))
        }

        // SOLO el calendario ocupa todo el ancho
        ContenedorCalendario()

        // Volvemos a los 20 dp para el resto
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            EstadoVacioEventos()
        }
    }
}

/**
 * Barra superior: icono de menú, título "Calendario" y botón "Agregar".
 * El icono de menú abre el Navigation Drawer mediante [onMenuClick].
 */
@Composable
private fun BarraSuperiorCalendario(onMenuClick: () -> Unit) {
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
            style = TipografiaStudyHub.TituloSeccion,
            modifier = Modifier.weight(1f)
        )

        BotonAgregar()
    }
}

/**
 * Botón "Agregar" con forma de píldora. Únicamente decorativo por ahora.
 */
@Composable
private fun BotonAgregar() {
    Box(
        modifier = Modifier
            .width(107.dp)
            .height(35.dp)
            .clip(RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Agregar",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Contenedor principal del calendario: navegación de mes, encabezado de
 * días de la semana y cuadrícula de días.
 */
@Composable
private fun ContenedorCalendario() {
    Column(
        modifier = Modifier
            .fillMaxWidth()

            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        NavegacionMes()

        Spacer(modifier = Modifier.height(16.dp))

        EncabezadoDiasSemana()

        Spacer(modifier = Modifier.height(8.dp))

        DiasAgosto2026.forEach { semana ->
            FilaDeDias(semana = semana)
        }
    }
}

/**
 * Encabezado con el nombre del mes actual y las flechas de navegación.
 * Las flechas son únicamente decorativas por ahora.
 */
@Composable
private fun NavegacionMes() {
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
        )

        Text(
            text = "Agosto 2026",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion
        )

        IconoFlecha(
            modifier = Modifier
                .size(width = 60.dp, height = 40.dp)
                .offset(x = 3.dp)
        )
    }
}

/**
 * Encabezado con las iniciales de los días de la semana.
 */
@Composable
private fun EncabezadoDiasSemana() {
    Row(modifier = Modifier.fillMaxWidth()) {
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

/**
 * Una fila (semana) de la cuadrícula de días del calendario.
 */
@Composable
private fun FilaDeDias(semana: List<Int?>) {
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
                    CeldaDia(dia = dia)
                }
            }
        }
    }
}

/**
 * Celda individual de un día dentro de la cuadrícula.
 * El día seleccionado se destaca con un círculo de acento.
 */
@Composable
private fun CeldaDia(dia: Int) {
    val estaSeleccionado = dia == DiaSeleccionado

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(
                if (estaSeleccionado) MaterialTheme.colorScheme.primary
                else androidx.compose.ui.graphics.Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dia.toString(),
            color = if (estaSeleccionado) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )
    }
}

/**
 * Estado vacío mostrado debajo del calendario cuando no existen eventos.
 */
@Composable
private fun EstadoVacioEventos() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 55.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.calendario_color),
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

@Preview(showBackground = true, backgroundColor = 0xFF111318)
@Composable
fun PantallaCalendarioPreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        PantallaCalendario()
    }
}