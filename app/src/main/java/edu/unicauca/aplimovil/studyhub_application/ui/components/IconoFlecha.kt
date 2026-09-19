package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

/**
 * Dirección hacia la que apunta la flecha triangular.
 */
enum class DireccionFlecha {
    IZQUIERDA,
    DERECHA
}

/**
 * Icono de flecha triangular construido mediante código (sin imagen externa).
 * Utilizado para la navegación visual entre meses del calendario.
 * Es únicamente decorativo: no ejecuta ninguna acción en esta etapa.
 */
@Composable
fun IconoFlecha(
    direccion: DireccionFlecha,
    modifier: Modifier = Modifier
) {
    val colorFlecha = MaterialTheme.colorScheme.onBackground

    Canvas(
        modifier = modifier
            .size(12.dp)
            .offset(x = if (direccion == DireccionFlecha.IZQUIERDA) 16.dp else (-16).dp)
    ) {
        val ancho = size.width
        val alto = size.height

        val trianguloPath = Path().apply {
            if (direccion == DireccionFlecha.IZQUIERDA) {
                moveTo(ancho, 0f)
                lineTo(0f, alto / 2f)
                lineTo(ancho, alto)
            } else {
                moveTo(0f, 0f)
                lineTo(ancho, alto / 2f)
                lineTo(0f, alto)
            }
            close()
        }

        drawPath(path = trianguloPath, color = colorFlecha)
    }
}