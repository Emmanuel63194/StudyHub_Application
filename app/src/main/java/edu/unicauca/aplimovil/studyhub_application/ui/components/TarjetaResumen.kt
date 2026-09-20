package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub

/**
 * Tarjeta reutilizable que representa el resumen de eventos de un día
 * (utilizada tanto por la sección "Hoy" como por la sección "Mañana").
 * Es puramente visual/decorativa: no ejecuta ninguna acción.
 */
@Composable
fun TarjetaResumen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconoLibreta()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Eventos pendientes",
                color = MaterialTheme.colorScheme.primary,
                style = TipografiaStudyHub.ResaltadoTarjeta
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.hombre_jardin),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No hay trabajos o examenes por realizar en el momento.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = TipografiaStudyHub.MensajeEstadoVacio,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Icono distintivo de "libreta/marcador".
 * Compuesto por: círculo base dibujado en código + imagen libreta_icono.png
 * centrada (12 x 15 dp), que reemplaza al rectángulo y al pequeño elemento
 * interior que antes se dibujaban en código.
 */
@Composable
private fun IconoLibreta(modifier: Modifier = Modifier) {
    val colorAcento = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(30.dp),
        contentAlignment = Alignment.Center
    ) {
        // Círculo base (30 x 30 dp), color de acento
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = colorAcento,
                radius = size.minDimension / 2f,
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }

        // Imagen superpuesta (12 x 15 dp), centrada: misma posición y tamaño
        // que tenía el rectángulo dibujado en código.
        Image(
            painter = painterResource(id = R.drawable.libreta_icono),
            contentDescription = null,
            modifier = Modifier.size(width = 12.dp, height = 15.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111318)
@Composable
fun TarjetaResumenPreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        TarjetaResumen()
    }
}