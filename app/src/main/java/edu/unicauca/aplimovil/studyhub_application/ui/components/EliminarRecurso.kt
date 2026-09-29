package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub

private val ColorModal = Color(0xFF191919)
private val ColorBotonCancelar = Color(0xFF0F0F0F)
private val ColorBotonEliminar = Color(0xFFACC6FF)
private val ColorTextoSecundario = Color(0xFF989898)

/**
 * Modal genérico de confirmación de eliminación.
 *
 * No sabe qué recurso se elimina: solo muestra la confirmación y
 * avisa la acción elegida mediante [onCancelar] y [onEliminar].
 */
@Composable
fun EliminarRecurso(
    onCancelar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier,
    titulo: String = "¿Quieres eliminarlo?",
    descripcion: String = "Eliminaras el cuestionario seleccionado"
) {
    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

        // Oscurece la pantalla de atrás.
        val ventana =
            (LocalView.current.parent as? DialogWindowProvider)?.window

        SideEffect {
            ventana?.setDimAmount(0.5f)
        }

        Column(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 444.dp)
                .height(191.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(ColorModal)
                .padding(
                    horizontal = 12.dp,
                    vertical = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = titulo,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = TipografiaStudyHub.TituloSeccion
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = descripcion,
                color = ColorTextoSecundario,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                BotonModal(
                    texto = "Cancelar",
                    colorFondo = ColorBotonCancelar,
                    colorTexto = Color.White,
                    onClick = onCancelar
                )

                BotonModal(
                    texto = "Eliminar",
                    colorFondo = ColorBotonEliminar,
                    colorTexto = Color.Black,
                    style = TipografiaStudyHub.ResaltadoTarjeta,
                    onClick = onEliminar
                )
            }
        }
    }
}

@Composable
private fun BotonModal(
    texto: String,
    colorFondo: Color,
    colorTexto: Color,
    style: TextStyle? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(
                width = 152.dp,
                height = 49.dp
            )
            .clip(RoundedCornerShape(5.dp))
            .background(colorFondo)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = texto,
            color = colorTexto,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            style = style ?: TextStyle(
                fontSize = 24.sp
            )
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF191919,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun EliminarRecursoPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(915.dp)
                .background(Color(0xFF191919)),
            contentAlignment = Alignment.Center
        ) {

            EliminarRecurso(
                onCancelar = {},
                onEliminar = {},
                titulo = "¿Quieres eliminarlo?",
                descripcion = "Eliminaras el cuestionario seleccionado"
            )
        }
    }
}
