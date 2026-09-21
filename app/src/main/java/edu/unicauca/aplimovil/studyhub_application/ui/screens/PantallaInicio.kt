package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.components.TarjetaResumen
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub

@Composable
fun PantallaInicio(onMenuClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconoHamburguesa(
                modifier = Modifier
                    .size(width = 34.dp, height = 34.dp)
                    .clickable(onClick = onMenuClick)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Resumen",
                color = MaterialTheme.colorScheme.onBackground,
                style = TipografiaStudyHub.TituloSeccion
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Hoy",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion
        )

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaResumen()

        Spacer(modifier = Modifier.height(28.dp))

        // Sección "Mañana"
        Text(
            text = "Mañana",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion
        )
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaResumen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111318)
@Composable
fun PantallaInicioPreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        PantallaInicio()
    }
}