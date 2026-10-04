package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import edu.unicauca.aplimovil.studyhub_application.ui.components.EliminarRecurso
import edu.unicauca.aplimovil.studyhub_application.ui.components.IconoHamburguesa
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.viewmodel.AsignaturaViewModel

@Composable
fun PantallaAsignaturas(
    viewModel: AsignaturaViewModel,
    onMenuClick: () -> Unit = {},
    onAgregarClick: () -> Unit = {},
    onAsignaturaClick: (AsignaturaEntity) -> Unit = {},
    onEditarClick: (AsignaturaEntity) -> Unit = {}
) {
    val lista by viewModel.asignaturas.collectAsState()

    var asignaturaAEliminar by remember { mutableStateOf<AsignaturaEntity?>(null) }

    ContenidoAsignaturas(
        lista = lista,
        onMenuClick = onMenuClick,
        onAgregarClick = onAgregarClick,
        onAsignaturaClick = onAsignaturaClick,
        onEditarClick = onEditarClick,
        onEliminarClick = { asignaturaAEliminar = it }
    )

    asignaturaAEliminar?.let { asignatura ->
        EliminarRecurso(
            titulo = "¿Quieres eliminarla?",
            descripcion = "Eliminarás la asignatura seleccionada",
            onCancelar = { asignaturaAEliminar = null },
            onEliminar = {
                viewModel.eliminar(asignatura)
                asignaturaAEliminar = null
            }
        )
    }
}

@Composable
private fun ContenidoAsignaturas(
    lista: List<AsignaturaEntity>,
    onMenuClick: () -> Unit,
    onAgregarClick: () -> Unit,
    onAsignaturaClick: (AsignaturaEntity) -> Unit,
    onEditarClick: (AsignaturaEntity) -> Unit,
    onEliminarClick: (AsignaturaEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        BarraSuperiorAsignaturas(onMenuClick = onMenuClick)

        if (lista.isEmpty()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .offset(x = 12.dp, y = (-45).dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EstadoVacioAsignaturas()
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),

                contentPadding = PaddingValues(bottom = 56.dp)
            ) {
                items(lista, key = { it.id }) { asignatura ->
                    TarjetaAsignatura(
                        nombre = asignatura.nombre,
                        salon = asignatura.salon,
                        onClick = { onAsignaturaClick(asignatura) },
                        onEditar = { onEditarClick(asignatura) },
                        onEliminar = { onEliminarClick(asignatura) }
                    )
                }
            }
        }

        SeccionAccion(onAgregarClick = onAgregarClick)
    }
}

@Composable
fun TarjetaAsignatura(
    nombre: String,
    salon: String,
    onClick: () -> Unit = {},
    onEditar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {

    val alturaTarjeta = if (salon.isNotEmpty()) {
        100.dp
    } else {
        76.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(alturaTarjeta)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier.offset(
                y = if (salon.isEmpty()) 4.dp else 0.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.calificacion_negro_icono),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = nombre,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.editar_icono),
                contentDescription = "Editar asignatura",
                modifier = Modifier.size(24.dp).clickable { onEditar() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(id = R.drawable.eliminar_icono),
                contentDescription = "Eliminar asignatura",
                modifier = Modifier.size(24.dp).clickable { onEliminar() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (salon.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ubicacion_icono),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = salon,
                    color = ColorTextoSecundarioEvento,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
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
private fun SeccionAccion(onAgregarClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-40).dp),
        horizontalArrangement = Arrangement.End
    ) {
        BotonAgregarAsignatura(onClick = onAgregarClick)
    }
}

@Composable
private fun BotonAgregarAsignatura(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111318)
@Composable
fun PantallaAsignaturasPreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        ContenidoAsignaturas(
            lista = emptyList(),
            onMenuClick = {},
            onAgregarClick = {},
            onAsignaturaClick = {},
            onEditarClick = {},
            onEliminarClick = {}
        )
    }
}