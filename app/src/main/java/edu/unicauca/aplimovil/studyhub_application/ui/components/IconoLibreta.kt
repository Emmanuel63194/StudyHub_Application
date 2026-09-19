package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

/**
 * Icono distintivo de "libreta/marcador" construido mediante código (sin imagen externa).
 * Compuesto por: círculo base + rectángulo superpuesto + pequeño elemento interior.
 */
@Composable
fun IconoLibreta(modifier: Modifier = Modifier) {
    val colorAcento = MaterialTheme.colorScheme.primary
    val colorContenido = MaterialTheme.colorScheme.onPrimary

    Canvas(modifier = modifier.size(30.dp)) {
        val radioCirculo = size.minDimension / 2f

        // Círculo base (30 x 30 px de referencia), color de acento
        drawCircle(
            color = colorAcento,
            radius = radioCirculo,
            center = Offset(size.width / 2f, size.height / 2f)
        )

        // Rectángulo superpuesto (12 x 15 px de referencia), color oscuro, centrado
        val anchoRectangulo = 12.dp.toPx()
        val altoRectangulo = 15.dp.toPx()
        val rectanguloX = (size.width - anchoRectangulo) / 2f
        val rectanguloY = (size.height - altoRectangulo) / 2f

        drawRect(
            color = colorContenido,
            topLeft = Offset(rectanguloX, rectanguloY),
            size = Size(anchoRectangulo, altoRectangulo)
        )

        // Pequeño elemento interior (3 x 4 px de referencia), esquina superior izquierda del rectángulo
        val anchoElemento = 3.dp.toPx()
        val altoElemento = 4.dp.toPx()

        drawRect(
            color = colorAcento,
            topLeft = Offset(rectanguloX + 2.dp.toPx(), rectanguloY),
            size = Size(anchoElemento, altoElemento)
        )
    }
}