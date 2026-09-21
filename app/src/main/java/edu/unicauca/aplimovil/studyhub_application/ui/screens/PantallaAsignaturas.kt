package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub

@Composable
fun PantallaAsignaturas(onMenuClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        BarraSuperiorAsignaturas(onMenuClick = onMenuClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .offset(x = 12.dp, y = (-45).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EstadoVacioAsignaturas()
        }

        SeccionAccion()
    }
}

@Composable
private fun BarraSuperiorAsignaturas(onMenuClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconoHamburguesa(
            modifier = Modifier
                .size(width = 34.dp, height = 34.dp)
                .clickable(onClick = onMenuClick)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Asignaturas",
            color = MaterialTheme.colorScheme.onBackground,
            style = TipografiaStudyHub.TituloSeccion
        )
    }
}

@Composable
private fun EstadoVacioAsignaturas() {
    Image(
        painter = painterResource(id = R.drawable.buzon_vacio),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(210.dp),
        contentScale = ContentScale.Fit
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Sin asignaturas",
        color = MaterialTheme.colorScheme.onBackground,
        style = TipografiaStudyHub.TituloSeccion,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Las asignaturas que añadas aparecerán aquí",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SeccionAccion() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-40).dp),
        horizontalArrangement = Arrangement.End
    ) {
        BotonAgregarAsignatura()
    }
}

@Composable
private fun BotonAgregarAsignatura() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111318)
@Composable
fun PantallaAsignaturasPreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        PantallaAsignaturas()
    }
}