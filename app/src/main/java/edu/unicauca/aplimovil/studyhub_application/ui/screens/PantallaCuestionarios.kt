package edu.unicauca.aplimovil.studyhub_application.ui.screens

import edu.unicauca.aplimovil.studyhub_application.ui.screens.ColorAcento
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity
import edu.unicauca.aplimovil.studyhub_application.ui.components.CuestionarioRapido
import edu.unicauca.aplimovil.studyhub_application.ui.components.EliminarRecurso
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.ui.viewmodel.CuestionarioViewModel

@Composable
fun PantallaCuestionarios(
    cuestionarios: List<CuestionarioEntity> = emptyList(),
    onMenuClick: () -> Unit = {},
    onAgregarClick: () -> Unit = {},
    onEditarClick: (CuestionarioEntity) -> Unit = {},
    onEliminarClick: (CuestionarioEntity) -> Unit = {},
    onCuestionarioClick: (CuestionarioEntity) -> Unit = {}
) {

    val cuestionarioViewModel: CuestionarioViewModel = viewModel()

    var cuestionarioSeleccionadoParaEliminar by remember {
        mutableStateOf<CuestionarioEntity?>(null)
    }

    var mostrarCuestionarioRapido by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            BarraSuperiorCuestionarios(
                onMenuClick = onMenuClick,
                onCuestionarioRapidoClick = {
                    mostrarCuestionarioRapido = true
                }
            )

            if (cuestionarios.isEmpty()) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .offset(x = 5.dp, y = (-75).dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EstadoVacioCuestionarios()
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        cuestionarios,
                        key = { it.id }
                    ) { cuestionario ->

                        TarjetaCuestionario(
                            cuestionario = cuestionario,
                            onEditarClick = {
                                onEditarClick(cuestionario)
                            },
                            onEliminarClick = {
                                cuestionarioSeleccionadoParaEliminar = cuestionario
                            },
                            onCuestionarioClick = {
                                onCuestionarioClick(cuestionario)
                            }
                        )
                    }
                }
            }
        }

        BotonAgregarCuestionario(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .offset(y = (-36).dp),
            onClick = onAgregarClick
        )

        cuestionarioSeleccionadoParaEliminar?.let { seleccionado ->
            EliminarRecurso(
                onCancelar = {
                    cuestionarioSeleccionadoParaEliminar = null
                },
                onEliminar = {
                    onEliminarClick(seleccionado)
                    cuestionarioSeleccionadoParaEliminar = null
                }
            )
        }

        if (mostrarCuestionarioRapido) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(
                            alpha = 0.5f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                CuestionarioRapido(
                    viewModel = cuestionarioViewModel,

                    onCerrar = {
                        mostrarCuestionarioRapido = false
                    },

                    onCuestionarioGuardado = {
                        mostrarCuestionarioRapido = false
                    }
                )
            }
        }
    }
}

@Composable
private fun BarraSuperiorCuestionarios(
    onMenuClick: () -> Unit,
    onCuestionarioRapidoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconoHamburguesa(
            modifier = Modifier
                .size(
                    width = 34.dp,
                    height = 34.dp
                )
                .clickable(
                    onClick = onMenuClick
                )
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            text = "Cuestionarios",
            color = Color.White,
            style = TipografiaStudyHub.TituloSeccion
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(
                id = R.drawable.envio_rapido
            ),
            contentDescription = "Cuestionario instantáneo",
            tint = Color.White,
            modifier = Modifier
                .offset(x = -11.dp)
                .size(30.dp)
                .clickable(
                    onClick = onCuestionarioRapidoClick
                )
        )
    }
}

@Composable
private fun TarjetaCuestionario(
    cuestionario: CuestionarioEntity,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onCuestionarioClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(67.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(
                onClick = onCuestionarioClick
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ColorAcento),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(
                    id = R.drawable.cuestionario_icono
                ),
                contentDescription = null,
                tint = ColorFondo,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = cuestionario.titulo,
            color = Color.White,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(
                id = R.drawable.editar_icono
            ),
            contentDescription = "Editar cuestionario",
            tint = Color.White,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(30.dp)
                .clickable(
                    onClick = onEditarClick
                )
        )

        Icon(
            painter = painterResource(
                id = R.drawable.eliminar_icono
            ),
            contentDescription = "Eliminar cuestionario",
            tint = Color.White,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(30.dp)
                .clickable(
                    onClick = onEliminarClick
                )
        )
    }
}

@Composable
private fun EstadoVacioCuestionarios() {

    Image(
        painter = painterResource(
            id = R.drawable.cuestionario_vacio
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(210.dp),
        contentScale = ContentScale.Fit
    )

    Spacer(
        modifier = Modifier.height(20.dp)
    )

    Text(
        text = "Sin cuestionarios",
        color = MaterialTheme.colorScheme.onBackground,
        style = TipografiaStudyHub.TituloSeccion,
        textAlign = TextAlign.Center
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Text(
        text = "Los cuestionarios que añadas aparecerán aquí",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun BotonAgregarCuestionario(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ColorAcento)
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "+",
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF191919
)
@Composable
fun PantallaCuestionariosPreview() {

    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        PantallaCuestionarios()
    }
}

