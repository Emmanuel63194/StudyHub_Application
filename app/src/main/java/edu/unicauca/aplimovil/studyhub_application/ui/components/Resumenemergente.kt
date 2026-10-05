package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import edu.unicauca.aplimovil.studyhub_application.ui.screens.ColorTextoSecundarioEvento
import edu.unicauca.aplimovil.studyhub_application.ui.screens.formatearFechaHoraEvento
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme

internal val ColorSuperficieResumen = Color(0xFF272727)

@Composable
fun ResumenEmergente(
    evento: CalendarioEntity,
    onCerrar: () -> Unit
) {

    val alturaMaxima = (LocalConfiguration.current.screenHeightDp * 0.6f).dp // Calcula una altura máxima equivalente al 60 % de la pantalla.

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = alturaMaxima)
            .background(MaterialTheme.colorScheme.surface)

            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.cerrar_icono),
                contentDescription = "Cerrar",
                modifier = Modifier
                    .size(38.dp)
                    .clickable(onClick = onCerrar)
            )

            Text(
                text = formatearFechaHoraEvento(evento),
                color = ColorTextoSecundarioEvento,
                fontSize = 16.sp
            )
        }


        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 8.dp)
        ) {

            Text(
                text = evento.titulo,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            if (evento.nota.isNotBlank()) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = evento.nota,
                    color = ColorTextoSecundarioEvento,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF111318,
    widthDp = 412,
    heightDp = 800
)
@Composable
private fun ResumenEmergentePreview() {

    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.BottomCenter
        ) {
            ResumenEmergente(
                evento = CalendarioEntity(
                    id = 1,
                    titulo = "Examen",
                    fecha = 1_786_000_000_000L,
                    hora = 960,
                    nota = "Tengo un examen importante de matemáticas. " +
                            "Debo estar preparado y recordar estudiar con anticipación."
                ),
                onCerrar = {}
            )
        }
    }
}