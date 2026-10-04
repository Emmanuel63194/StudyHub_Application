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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.PreguntaCuestionario
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.RespuestaCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.ui.viewmodel.CuestionarioViewModel

// Paleta de las pantallas de cuestionarios (también usada por PantallaCuestionarios).
internal val ColorFondo = Color(0xFF191919)
internal val ColorSuperficie = Color(0xFF272727)
internal val ColorAcento = Color(0xFFACC6FF)
internal val ColorTextoSecundario = Color(0xFF989898)
private val ColorError = Color(0xFFFF3B47)

@Composable
fun CrearCuestionario(
    onVolver: () -> Unit,
    onCreado: () -> Unit,
    cuestionarioAEditar: CuestionarioEntity? = null,
    viewModel: CuestionarioViewModel = viewModel()
) {
    val titulo by viewModel.titulo.collectAsState()
    val preguntas by viewModel.preguntas.collectAsState()
    val intentoCrear by viewModel.intentoCrear.collectAsState()

    LaunchedEffect(cuestionarioAEditar?.id) {
        cuestionarioAEditar?.let {
            viewModel.cargarCuestionarioParaEditar(it)
        }
    }

    ContenidoCrearCuestionario(
        titulo = titulo,
        preguntas = preguntas,
        intentoCrear = intentoCrear,
        alCambiarTitulo = viewModel::actualizarTitulo,
        mensajeErrorPregunta = viewModel::mensajeErrorPregunta,
        alCambiarPregunta = { indicePregunta, texto ->
            viewModel.actualizarTextoPregunta(
                indicePregunta,
                texto
            )
        },
        alEliminarPregunta = { indicePregunta ->
            viewModel.eliminarPregunta(indicePregunta)
        },
        alCambiarRespuesta = { indicePregunta, indiceRespuesta, texto ->
            viewModel.actualizarTextoRespuesta(
                indicePregunta,
                indiceRespuesta,
                texto
            )
        },
        alMarcarCorrecta = { indicePregunta, indiceRespuesta ->
            viewModel.marcarRespuestaCorrecta(
                indicePregunta,
                indiceRespuesta
            )
        },
        alEliminarRespuesta = { indicePregunta, indiceRespuesta ->
            viewModel.eliminarRespuesta(
                indicePregunta,
                indiceRespuesta
            )
        },
        alAgregarPregunta = {
            viewModel.agregarPregunta()
        },
        onVolver = onVolver,
        onCrear = {
            if (cuestionarioAEditar == null) {
                viewModel.intentarCrear {
                    onCreado()
                }
            } else {
                viewModel.editarCuestionario(
                    cuestionario = cuestionarioAEditar,
                    onGuardado = {
                        onCreado()
                    }
                )
            }
        },
        modoEdicion = cuestionarioAEditar != null
    )
}

@Composable
private fun ContenidoCrearCuestionario(
    titulo: String,
    preguntas: List<PreguntaCuestionario>,
    intentoCrear: Boolean,
    alCambiarTitulo: (String) -> Unit,
    mensajeErrorPregunta: (PreguntaCuestionario) -> String?,
    alCambiarPregunta: (Int, String) -> Unit,
    alEliminarPregunta: (Int) -> Unit,
    alCambiarRespuesta: (Int, Int, String) -> Unit,
    alMarcarCorrecta: (Int, Int) -> Unit,
    alEliminarRespuesta: (Int, Int) -> Unit,
    alAgregarPregunta: () -> Unit,
    onVolver: () -> Unit,
    onCrear: () -> Unit,
    modoEdicion: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
    ) {

        BarraSuperiorCrear(
            onVolver = onVolver,
            onCrear = onCrear,
            modoEdicion = modoEdicion
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
                alCambiar = alCambiarTitulo,
                placeholder = "Añadir nombre"
            )

            Spacer(modifier = Modifier.height(2.dp))

            preguntas.forEachIndexed { indicePregunta, pregunta ->

                BloquePregunta(
                    indice = indicePregunta,
                    pregunta = pregunta,
                    error = if (intentoCrear) {
                        mensajeErrorPregunta(pregunta)
                    } else {
                        null
                    },
                    alCambiarTexto = { texto ->
                        alCambiarPregunta(
                            indicePregunta,
                            texto
                        )
                    },
                    alEliminarPregunta = {
                        alEliminarPregunta(indicePregunta)
                    },
                    alCambiarRespuesta = { indiceRespuesta, texto ->
                        alCambiarRespuesta(
                            indicePregunta,
                            indiceRespuesta,
                            texto
                        )
                    },
                    alMarcarCorrecta = { indiceRespuesta ->
                        alMarcarCorrecta(
                            indicePregunta,
                            indiceRespuesta
                        )
                    },
                    alEliminarRespuesta = { indiceRespuesta ->
                        alEliminarRespuesta(
                            indicePregunta,
                            indiceRespuesta
                        )
                    }
                )

                if (indicePregunta < preguntas.lastIndex) {
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }


            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(49.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorAcento)
                    .clickable(
                        onClick = alAgregarPregunta
                    ),
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
private fun BarraSuperiorCrear(
    onVolver: () -> Unit,
    onCrear: () -> Unit,
    modoEdicion: Boolean = false
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
            text = if (modoEdicion) "Editar cuestionario" else "Crear cuestionario",
            color = Color.White,
            fontSize = 23.sp,
            modifier = Modifier.weight(1f),
            style = TipografiaStudyHub.TituloSeccion
        )

        Box(
            modifier = Modifier
                .size(
                    width = 95.dp,
                    height = 36.dp
                )
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(ColorAcento)
                .clickable(
                    onClick = onCrear
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (modoEdicion) "Editar" else "Crear",
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
private fun BloquePregunta(
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

                IconoAccion(
                    recurso = if (respuesta.esCorrecta) {
                        R.drawable.check_verde_icono
                    } else {
                        R.drawable.check_icono
                    },
                    descripcion = "Marcar como correcta",
                    conservarColor = respuesta.esCorrecta,
                    onClick = {
                        alMarcarCorrecta(
                            indiceRespuesta
                        )
                    }
                )

                if (
                    pregunta.respuestas.size == 3 &&
                    indiceRespuesta == 2
                ) {
                    IconoAccion(
                        recurso = R.drawable.eliminar_icono,
                        descripcion = "Eliminar respuesta",
                        onClick = {
                            alEliminarRespuesta(
                                indiceRespuesta
                            )
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
            .background(MaterialTheme.colorScheme.surfaceContainer)
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

@Preview(
    showBackground = true,
    backgroundColor = 0xFF191919,
    widthDp = 412,
    heightDp = 915
)
@Composable
fun CrearCuestionarioPreview() {

    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        ContenidoCrearCuestionario(
            titulo = "Electromagnetismo",

            preguntas = listOf(
                PreguntaCuestionario(
                    texto = "¿Cuál es la unidad de fuerza en el Sistema Internacional?",
                    respuestas = listOf(
                        RespuestaCuestionario(
                            texto = "Newton",
                            esCorrecta = true
                        ),
                        RespuestaCuestionario(
                            texto = "Joule",
                            esCorrecta = false
                        ),
                        RespuestaCuestionario(
                            texto = "Watt",
                            esCorrecta = false
                        )
                    )
                )
            ),

            intentoCrear = false,

            alCambiarTitulo = {},
            mensajeErrorPregunta = { null },
            alCambiarPregunta = { _, _ -> },
            alEliminarPregunta = {},
            alCambiarRespuesta = { _, _, _ -> },
            alMarcarCorrecta = { _, _ -> },
            alEliminarRespuesta = { _, _ -> },
            alAgregarPregunta = {},

            onVolver = {},
            onCrear = {}
        )
    }
}