package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AcentoPrincipal
import edu.unicauca.aplimovil.studyhub_application.ui.theme.IconoOscuro

/**
 * Icono distintivo de "libreta/marcador" construido mediante código (sin imagen externa).
 * Compuesto por: círculo base + rectángulo superpuesto + pequeño elemento interior.
 */
@Composable
fun IconoLibreta(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(30.dp)) {
        val radioCirculo = size.minDimension / 2f

        // Círculo base (30 x 30 px de referencia), color de acento
        drawCircle(
            color = AcentoPrincipal,
            radius = radioCirculo,
            center = Offset(size.width / 2f, size.height / 2f)
        )

        // Rectángulo superpuesto (12 x 15 px de referencia), color negro, centrado
        val anchoRectangulo = 12.dp.toPx()
        val altoRectangulo = 15.dp.toPx()
        val rectanguloX = (size.width - anchoRectangulo) / 2f
        val rectanguloY = (size.height - altoRectangulo) / 2f

        drawRect(
            color = IconoOscuro,
            topLeft = Offset(rectanguloX, rectanguloY),
            size = Size(anchoRectangulo, altoRectangulo)
        )

        // Pequeño elemento interior (3 x 4 px de referencia), esquina superior izquierda del rectángulo
        val anchoElemento = 3.dp.toPx()
        val altoElemento = 4.dp.toPx()

        drawRect(
            color = AcentoPrincipal,
            topLeft = Offset(rectanguloX + 2.dp.toPx(), rectanguloY),
            size = Size(anchoElemento, altoElemento)
        )
    }
}
