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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.components.TarjetaResumen
import edu.unicauca.aplimovil.studyhub_application.ui.theme.FondoPrincipal
import edu.unicauca.aplimovil.studyhub_application.ui.theme.StudyHub_ApplicationTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TextoPrincipal

/**
 * Pantalla principal (Resumen) de StudyHub.
 *
 * [onMenuClick] se invoca al pulsar el ícono de hamburguesa y permite que
 * quien aloje esta pantalla (MainActivity) abra el Navigation Drawer
 * centralizado de la aplicación.
 */
@Composable
fun PantallaInicio(onMenuClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Encabezado: menú hamburguesa + título "Resumen"
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconoHamburguesa(
                modifier = Modifier
                    .size(width = 24.dp, height = 15.dp)
                    .clickable(onClick = onMenuClick)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Resumen",
                color = TextoPrincipal,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección "Hoy"
        Text(
            text = "Hoy",
            color = TextoPrincipal,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaResumen()

        Spacer(modifier = Modifier.height(28.dp))

        // Sección "Mañana"
        Text(
            text = "Mañana",
            color = TextoPrincipal,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaResumen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF191919)
@Composable
fun PantallaInicioPreview() {
    StudyHub_ApplicationTheme {
        PantallaInicio()
    }
}
