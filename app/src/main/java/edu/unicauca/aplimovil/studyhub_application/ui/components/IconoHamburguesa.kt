package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import edu.unicauca.aplimovil.studyhub_application.R

@Composable
fun IconoHamburguesa(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.hamburguesa_icono),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds
    )
}