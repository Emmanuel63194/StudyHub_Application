package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import edu.unicauca.aplimovil.studyhub_application.R

/**
 * Icono de flecha basado en la imagen flecha_icono.png.
 * El tamaño, la posición y la rotación los define quien lo usa mediante [modifier].
 * Es únicamente decorativo: no ejecuta ninguna acción.
 */
@Composable
fun IconoFlecha(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.flecha_icono),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}