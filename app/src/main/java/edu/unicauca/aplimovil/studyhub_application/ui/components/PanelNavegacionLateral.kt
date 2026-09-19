package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.ui.theme.FondoPrincipal
import edu.unicauca.aplimovil.studyhub_application.ui.theme.SeleccionMenuLateral
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TextoPrincipal

/**
 * Identifica cada una de las pantallas navegables desde el menú lateral.
 */
enum class PantallaSeleccionada {
    RESUMEN,
    CALENDARIO,
    ASIGNATURAS,
    CALIFICACIONES,
    CUESTIONARIOS
}

/**
 * Representa una opción individual del menú lateral.
 */
private data class OpcionMenu(
    val pantalla: PantallaSeleccionada,
    val texto: String,
    val iconoResId: Int,
    val implementada: Boolean
)

private val OpcionesMenu = listOf(
    OpcionMenu(PantallaSeleccionada.RESUMEN, "Resumen", R.drawable.casa_home, implementada = true),
    OpcionMenu(PantallaSeleccionada.CALENDARIO, "Calendario", R.drawable.calendario_icono, implementada = true),
    OpcionMenu(PantallaSeleccionada.ASIGNATURAS, "Asignaturas", R.drawable.asignatura_icono, implementada = true),
    OpcionMenu(PantallaSeleccionada.CALIFICACIONES, "Calificaciones", R.drawable.calificacion_icono, implementada = true),
    OpcionMenu(PantallaSeleccionada.CUESTIONARIOS, "Cuestionarios", R.drawable.cuestionario_icono, implementada = true)
)

/**
 * Contenido del Navigation Drawer lateral de StudyHub.
 *
 * Muestra el encabezado de la aplicación, la lista de opciones de
 * navegación (marcando cuál corresponde a la pantalla actual) y el logo
 * en la parte inferior. La navegación real y el cierre del panel se
 * delegan al llamador mediante [alSeleccionarOpcion].
 */
@Composable
fun PanelNavegacionLateral(
    pantallaActual: PantallaSeleccionada,
    alSeleccionarOpcion: (PantallaSeleccionada) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(FondoPrincipal)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Agenda escolar",
            color = TextoPrincipal,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box1PxDivisor()

        Spacer(modifier = Modifier.height(16.dp))

        OpcionesMenu.forEach { opcion ->
            ElementoMenuLateral(
                opcion = opcion,
                seleccionado = opcion.pantalla == pantallaActual,
                onClick = {
                    if (opcion.implementada) {
                        alSeleccionarOpcion(opcion.pantalla)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.logo_principal),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .offset(y = (-30).dp),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Línea divisoria entre el encabezado y las opciones de navegación.
 */
@Composable
private fun Box1PxDivisor() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TextoPrincipal)
    )
}

/**
 * Elemento individual de la lista de navegación: icono + texto, con
 * fondo resaltado cuando corresponde a la pantalla actualmente activa.
 */
@Composable
private fun ElementoMenuLateral(
    opcion: OpcionMenu,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (seleccionado) SeleccionMenuLateral else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = opcion.iconoResId),
            contentDescription = null,
            modifier = Modifier.size(26.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = opcion.texto,
            color = TextoPrincipal,
            fontSize = 20.sp
        )
    }
}
