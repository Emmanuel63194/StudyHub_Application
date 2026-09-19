package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TextoPrincipal

/**
 * Icono de menú hamburguesa construido mediante código (sin imagen externa).
 * Es únicamente decorativo: no ejecuta ninguna acción en esta etapa.
 */
@Composable
fun IconoHamburguesa(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val grosorLinea = 2.dp.toPx()
        val separacion = size.height / 2f

        for (i in 0..2) {
            val y = i * separacion
            drawLine(
                color = TextoPrincipal,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = grosorLinea,
                cap = StrokeCap.Round
            )
        }
    }
}
