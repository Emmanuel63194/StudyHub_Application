package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaInicio
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme

enum class PantallaSeleccionada {
    RESUMEN,
    CALENDARIO,
    ASIGNATURAS,
    CALIFICACIONES,
    CUESTIONARIOS
}

private data class OpcionMenu(
    val pantalla: PantallaSeleccionada,
    val texto: String,
    val iconoResId: Int
)

private val OpcionesMenu = listOf(
    OpcionMenu(PantallaSeleccionada.RESUMEN, "Resumen", R.drawable.casa_home),
    OpcionMenu(PantallaSeleccionada.CALENDARIO, "Calendario", R.drawable.calendario_icono),
    OpcionMenu(PantallaSeleccionada.ASIGNATURAS, "Asignaturas", R.drawable.asignatura_icono),
    OpcionMenu(PantallaSeleccionada.CALIFICACIONES, "Calificaciones", R.drawable.calificacion_icono),
    OpcionMenu(PantallaSeleccionada.CUESTIONARIOS, "Cuestionarios", R.drawable.cuestionario_icono)
)

@Composable
fun PanelNavegacionLateral(
    pantallaActual: PantallaSeleccionada,
    alSeleccionarOpcion: (PantallaSeleccionada) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Agenda escolar",
            color = MaterialTheme.colorScheme.onBackground,
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
                    alSeleccionarOpcion(opcion.pantalla)
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

@Composable
private fun Box1PxDivisor() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onBackground)
    )
}

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
            .background(
                if (seleccionado) MaterialTheme.colorScheme.primaryContainer
                else androidx.compose.ui.graphics.Color.Transparent
            )
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
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VistaPreviaMenuConPantalla() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            PantallaInicio(
                onMenuClick = {}
            )

            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)
                    )
            )

            PanelNavegacionLateral(
                pantallaActual = PantallaSeleccionada.RESUMEN,
                alSeleccionarOpcion = {},
                modifier = Modifier.width(310.dp)
            )
        }
    }
}