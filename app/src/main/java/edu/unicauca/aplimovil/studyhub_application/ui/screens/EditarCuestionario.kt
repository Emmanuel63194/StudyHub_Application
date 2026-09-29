package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.PreguntaCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.ui.viewmodel.CuestionarioViewModel

private val ColorError = Color(0xFFFF3B47)

@Composable
fun EditarCuestionario(
    cuestionario: CuestionarioEntity,
    onVolver: () -> Unit,
    onEditado: () -> Unit,
    viewModel: CuestionarioViewModel = viewModel()
) {
    val titulo by viewModel.titulo.collectAsState()
    val preguntas by viewModel.preguntas.collectAsState()
    val intentoCrear by viewModel.intentoCrear.collectAsState()

    /*
     * Cuando se abre esta pantalla se cargan los datos
     * del cuestionario seleccionado.
     */
    LaunchedEffect(cuestionario.id) {
        viewModel.cargarCuestionarioParaEditar(cuestionario)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .statusBarsPadding()
            .imePadding()
    ) {
        BarraSuperiorEditar(
            onVolver = onVolver,
            onEditar = {
                viewModel.editarCuestionario(
                    cuestionario = cuestionario,
                    onGuardado = {
                        onEditado()
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ----- Nombre -----

            EncabezadoConError(
                titulo = "Nombre:",
                error = if (intentoCrear && titulo.isBlank()) {
                    "(Falta el nombre del cuestionario)"
                } else {
                    null
                }
            )

            CampoTextoCuestionario(
                valor = titulo,
                alCambiar = viewModel::actualizarTitulo,
                placeholder = "Añadir nombre"
            )

            // ----- Preguntas -----

            Spacer(modifier = Modifier.height(2.dp))

            preguntas.forEachIndexed { indicePregunta, pregunta ->

                BloquePreguntaEditar(
                    indice = indicePregunta,
                    pregunta = pregunta,
                    error = if (intentoCrear) {
                        viewModel.mensajeErrorPregunta(pregunta)
                    } else {
                        null
                    },
                    alCambiarTexto = {
                        viewModel.actualizarTextoPregunta(
                            indicePregunta,
                            it
                        )
                    },
                    alEliminarPregunta = {
                        viewModel.eliminarPregunta(indicePregunta)
                    },
                    alCambiarRespuesta = { indiceRespuesta, texto ->
                        viewModel.actualizarTextoRespuesta(
                            indicePregunta,
                            indiceRespuesta,
                            texto
                        )
                    },
                    alMarcarCorrecta = {
                        viewModel.marcarRespuestaCorrecta(
                            indicePregunta,
                            it
                        )
                    },
                    alEliminarRespuesta = {
                        viewModel.eliminarRespuesta(
                            indicePregunta,
                            it
                        )
                    }
                )

                // Espacio entre bloques de preguntas
                if (indicePregunta < preguntas.lastIndex) {
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }
            // ----- Agregar pregunta -----

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(49.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorAcento)
                    .clickable {
                        viewModel.agregarPregunta()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = Color.Black,
                    fontSize = 24.sp
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun BarraSuperiorEditar(
    onVolver: () -> Unit,
    onEditar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(
                id = R.drawable.regresar_icono
            ),
            contentDescription = "Volver",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .clickable(
                    onClick = onVolver
                )
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            text = "Editar cuestionario",
            color = Color.White,
            fontSize = 23.sp,
            modifier = Modifier.weight(1f),
            style = TipografiaStudyHub.TituloSeccion
        )

        Box(
            modifier = Modifier
                .size(
                    width = 107.dp,
                    height = 36.dp
                )
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(ColorAcento)
                .clickable(
                    onClick = onEditar
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Editar",
                color = Color.Black,
                style = TipografiaStudyHub.ResaltadoTarjeta
            )
        }
    }
}

@Composable
private fun EncabezadoConError(
    titulo: String,
    error: String?,
    contenidoFinal: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = titulo,
            color = Color.White,
            fontSize = 20.sp
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = error ?: "",
            color = ColorError,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )

        contenidoFinal()
    }
}

@Composable
private fun BloquePreguntaEditar(
    indice: Int,
    pregunta: PreguntaCuestionario,
    error: String?,
    alCambiarTexto: (String) -> Unit,
    alEliminarPregunta: () -> Unit,
    alCambiarRespuesta: (Int, String) -> Unit,
    alMarcarCorrecta: (Int) -> Unit,
    alEliminarRespuesta: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        EncabezadoConError(
            titulo = "Pregunta ${indice + 1}",
            error = error
        ) {

            // La primera pregunta no se puede eliminar.
            if (indice > 0) {
                IconoAccion(
                    recurso = R.drawable.eliminar_icono,
                    descripcion = "Eliminar pregunta",
                    onClick = alEliminarPregunta
                )
            }
        }

        CampoTextoCuestionario(
            valor = pregunta.texto,
            alCambiar = alCambiarTexto,
            placeholder = "Escribe la pregunta"
        )

        pregunta.respuestas.forEachIndexed { indiceRespuesta, respuesta ->

            CampoTextoCuestionario(
                valor = respuesta.texto,
                alCambiar = {
                    alCambiarRespuesta(
                        indiceRespuesta,
                        it
                    )
                },
                placeholder = "Escribe la respuesta"
            ) {

                /*
                 * Si la respuesta existente es correcta,
                 * se muestra directamente el check verde.
                 */
                IconoAccion(
                    recurso = if (respuesta.esCorrecta) {
                        R.drawable.check_verde_icono
                    } else {
                        R.drawable.check_icono
                    },
                    descripcion = "Marcar como correcta",
                    conservarColor = respuesta.esCorrecta,
                    onClick = {
                        alMarcarCorrecta(indiceRespuesta)
                    }
                )

                /*
                 * Solo se puede eliminar la tercera respuesta,
                 * para mantener como mínimo dos respuestas.
                 */
                if (
                    pregunta.respuestas.size == 3 &&
                    indiceRespuesta == 2
                ) {
                    IconoAccion(
                        recurso = R.drawable.eliminar_icono,
                        descripcion = "Eliminar respuesta",
                        onClick = {
                            alEliminarRespuesta(indiceRespuesta)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun IconoAccion(
    recurso: Int,
    descripcion: String,
    onClick: () -> Unit,
    conservarColor: Boolean = false
) {
    Icon(
        painter = painterResource(
            id = recurso
        ),
        contentDescription = descripcion,
        tint = if (conservarColor) {
            Color.Unspecified
        } else {
            Color.White
        },
        modifier = Modifier
            .padding(start = 8.dp)
            .size(30.dp)
            .clickable(
                onClick = onClick
            )
    )
}

@Composable
private fun CampoTextoCuestionario(
    valor: String,
    alCambiar: (String) -> Unit,
    placeholder: String,
    contenidoFinal: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(67.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(ColorSuperficie)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {

            if (valor.isEmpty()) {
                Text(
                    text = placeholder,
                    color = ColorTextoSecundario,
                    fontSize = 20.sp
                )
            }

            BasicTextField(
                value = valor,
                onValueChange = alCambiar,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier.fillMaxWidth()
            )
        }

        contenidoFinal()
    }
}
