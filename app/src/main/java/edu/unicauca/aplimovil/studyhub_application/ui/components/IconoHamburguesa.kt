package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import edu.unicauca.aplimovil.studyhub_application.R

/**
 * Icono de menú hamburguesa basado en la imagen hamburguesa_icono.png.
 * El tamaño y el comportamiento (clickable) los define quien lo usa
 * mediante [modifier] (24 x 15 dp en las pantallas).
 * Es únicamente decorativo: no ejecuta ninguna acción por sí mismo.
 */
@Composable
fun IconoHamburguesa(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.hamburguesa_icono),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}