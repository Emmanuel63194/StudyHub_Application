package edu.unicauca.aplimovil.studyhub_application.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import edu.unicauca.aplimovil.studyhub_application.BuildConfig
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.PreguntaCuestionario
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.RespuestaCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.ui.viewmodel.CuestionarioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val ColorTextoError = Color(0xFFFF3B47)

@Composable
fun CuestionarioRapido(
    viewModel: CuestionarioViewModel,
    onCerrar: () -> Unit = {},
    onCuestionarioGuardado: () -> Unit = {}
) {
    var contenido by remember {
        mutableStateOf("")
    }

    var cantidadPreguntas by remember {
        mutableStateOf("")
    }

    var mostrarErrorContenido by remember {
        mutableStateOf(false)
    }

    var mostrarErrorCantidad by remember {
        mutableStateOf(false)
    }

    var generando by remember {
        mutableStateOf(false)
    }

    val alcanceCorutinas = rememberCoroutineScope()

    Dialog(
        onDismissRequest = {
            if (!generando) {
                onCerrar()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

        val ventana =
            (LocalView.current.parent as? DialogWindowProvider)?.window

        SideEffect {
            ventana?.setDimAmount(0.5f)
        }

        Box(
            modifier = Modifier
                .size(
                    width = 390.dp,
                    height = 380.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(15.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {

                    Icon(
                        painter = painterResource(
                            id = R.drawable.cerrar_icono
                        ),
                        contentDescription = "Cerrar",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(30.dp)
                            .clickable(
                                enabled = !generando,
                                onClick = onCerrar
                            )
                    )
                }

                Text(
                    text = "Cuestionario instantaneo",
                    color = Color.White,
                    fontSize = 24.sp
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                CampoContenidoTema(
                    texto = contenido,
                    mostrarError = mostrarErrorContenido,
                    habilitado = !generando,
                    onTextoCambiado = {
                        contenido = it
                        mostrarErrorContenido = false
                    }
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                CampoCantidadPreguntas(
                    texto = cantidadPreguntas,
                    mostrarError = mostrarErrorCantidad,
                    habilitado = !generando,
                    onTextoCambiado = {
                        cantidadPreguntas = it
                        mostrarErrorCantidad = false
                    }
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                if (generando) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = Color(0xFFACC6FF),
                        strokeWidth = 3.dp
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .size(
                                width = 107.dp,
                                height = 36.dp
                            )
                            .background(
                                color = Color(0xFFACC6FF),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {

                                mostrarErrorContenido =
                                    contenido.isBlank()

                                mostrarErrorCantidad =
                                    cantidadPreguntas.isBlank()

                                val cantidad =
                                    cantidadPreguntas.toIntOrNull()

                                if (
                                    contenido.isNotBlank() &&
                                    cantidad != null &&
                                    cantidad > 0
                                ) {

                                    mostrarErrorCantidad = false
                                    generando = true

                                    alcanceCorutinas.launch {

                                        val resultado =
                                            generarCuestionarioOpenRouter(
                                                texto = contenido,
                                                cantidadPreguntas = cantidad
                                            )

                                        if (
                                            resultado.titulo != null &&
                                            resultado.preguntas != null
                                        ) {

                                            viewModel.guardarCuestionarioGenerado(
                                                titulo = resultado.titulo,
                                                preguntas = resultado.preguntas
                                            ) {

                                                generando = false

                                                onCuestionarioGuardado()
                                            }

                                        } else {

                                            generando = false
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Generar",
                            style = TipografiaStudyHub.TituloSeccion,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CampoContenidoTema(
    texto: String,
    mostrarError: Boolean,
    habilitado: Boolean,
    onTextoCambiado: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .size(
                    width = 350.dp,
                    height = 156.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {

            BasicTextField(
                value = texto,
                onValueChange = onTextoCambiado,
                enabled = habilitado,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp
                ),
                decorationBox = { campoTexto ->

                    if (texto.isEmpty()) {

                        Text(
                            text = "Pegue aqui la informacion de los temas",
                            color = Color(0xFF989898),
                            fontSize = 20.sp
                        )
                    }

                    campoTexto()
                }
            )
        }

        if (mostrarError) {

            Text(
                text = "Falta contenido del tema",
                color = ColorTextoError,
                fontSize = 14.sp,
                modifier = Modifier.padding(
                    start = 4.dp,
                    top = 4.dp
                )
            )
        }
    }
}

@Composable
private fun CampoCantidadPreguntas(
    texto: String,
    mostrarError: Boolean,
    habilitado: Boolean,
    onTextoCambiado: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .size(
                    width = 350.dp,
                    height = 44.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(
                    horizontal = 14.dp
                ),
            contentAlignment = Alignment.CenterStart
        ) {

            BasicTextField(
                value = texto,
                onValueChange = {

                    if (
                        it.all { caracter ->
                            caracter.isDigit()
                        }
                    ) {
                        onTextoCambiado(it)
                    }
                },
                enabled = habilitado,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                decorationBox = { campoTexto ->

                    if (texto.isEmpty()) {

                        Text(
                            text = "Digite la cantidad de preguntas",
                            color = Color(0xFF989898),
                            fontSize = 18.sp
                        )
                    }

                    campoTexto()
                }
            )
        }

        if (mostrarError) {

            Text(
                text = "Falta numero de preguntas",
                color = ColorTextoError,
                fontSize = 14.sp,
                modifier = Modifier.padding(
                    start = 4.dp,
                    top = 4.dp
                )
            )
        }
    }
}

private data class ResultadoCuestionarioGenerado(
    val titulo: String?,
    val preguntas: List<PreguntaCuestionario>?,
    val mensaje: String
)

private suspend fun generarCuestionarioOpenRouter(
    texto: String,
    cantidadPreguntas: Int
): ResultadoCuestionarioGenerado =
    withContext(Dispatchers.IO) {

        var conexion: HttpURLConnection? = null

        try {

            val apiKey =
                BuildConfig.OPENROUTER_API_KEY

            if (apiKey.isBlank()) {

                return@withContext ResultadoCuestionarioGenerado(
                    titulo = null,
                    preguntas = null,
                    mensaje = "No se encontró la API Key de OpenRouter."
                )
            }

            val url = URL(
                "https://openrouter.ai/api/v1/chat/completions"
            )

            conexion =
                url.openConnection() as HttpURLConnection

            conexion.requestMethod = "POST"
            conexion.connectTimeout = 30000
            conexion.readTimeout = 120000

            conexion.setRequestProperty(
                "Authorization",
                "Bearer $apiKey"
            )

            conexion.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            conexion.doOutput = true

            val instrucciones = """
                A partir exclusivamente del texto de estudio
                proporcionado, genera un cuestionario.

                Primero genera un título breve y descriptivo
                relacionado directamente con el contenido del texto.

                Después genera exactamente $cantidadPreguntas
                preguntas de selección múltiple.

                Cada pregunta debe tener exactamente 3 opciones.

                Cada pregunta debe tener una única respuesta correcta.

                No inventes información que no aparezca o que no
                pueda deducirse razonablemente del texto proporcionado.

                La respuesta debe ser ÚNICAMENTE un objeto JSON válido.

                No escribas explicaciones.
                No escribas Markdown.
                No utilices bloques de código.
                No escribas texto antes ni después del JSON.

                Utiliza exactamente esta estructura:

                {
                  "titulo": "Título generado según el contenido",
                  "preguntas": [
                    {
                      "pregunta": "Texto de la pregunta",
                      "opciones": [
                        "Opción A",
                        "Opción B",
                        "Opción C"
                      ],
                      "respuestaCorrecta": 0
                    }
                  ]
                }

                El campo "respuestaCorrecta" debe contener:

                0 si la respuesta correcta es la primera opción.
                1 si la respuesta correcta es la segunda opción.
                2 si la respuesta correcta es la tercera opción.

                TEXTO DE ESTUDIO:

                $texto
            """.trimIndent()

            val cuerpo = JSONObject().apply {

                put(
                    "model",
                    "poolside/laguna-s-2.1:free"
                )

                put(
                    "messages",
                    JSONArray().apply {

                        put(
                            JSONObject().apply {

                                put(
                                    "role",
                                    "user"
                                )

                                put(
                                    "content",
                                    instrucciones
                                )
                            }
                        )
                    }
                )
            }

            conexion.outputStream.use { salida ->

                salida.write(
                    cuerpo.toString().toByteArray(
                        Charsets.UTF_8
                    )
                )
            }

            val codigoRespuesta =
                conexion.responseCode

            val flujo =
                if (codigoRespuesta in 200..299) {
                    conexion.inputStream
                } else {
                    conexion.errorStream
                }

            val textoRespuesta =
                flujo
                    ?.bufferedReader()
                    ?.use {
                        it.readText()
                    }
                    ?: ""

            if (codigoRespuesta !in 200..299) {

                return@withContext ResultadoCuestionarioGenerado(
                    titulo = null,
                    preguntas = null,
                    mensaje = """
                        Error HTTP $codigoRespuesta

                        $textoRespuesta
                    """.trimIndent()
                )
            }

            val respuestaApi =
                JSONObject(textoRespuesta)

            val choices =
                respuestaApi.optJSONArray("choices")

            if (
                choices == null ||
                choices.length() == 0
            ) {

                return@withContext ResultadoCuestionarioGenerado(
                    titulo = null,
                    preguntas = null,
                    mensaje = """
                        La API respondió, pero no se encontró
                        ninguna respuesta del modelo.
                    """.trimIndent()
                )
            }

            val mensaje =
                choices
                    .getJSONObject(0)
                    .getJSONObject("message")

            val contenido =
                mensaje
                    .optString(
                        "content",
                        ""
                    )
                    .trim()

            if (contenido.isBlank()) {

                return@withContext ResultadoCuestionarioGenerado(
                    titulo = null,
                    preguntas = null,
                    mensaje = """
                        La API respondió correctamente,
                        pero el modelo no devolvió contenido.
                    """.trimIndent()
                )
            }

            procesarJSONCuestionario(
                contenido = contenido,
                cantidadEsperada = cantidadPreguntas
            )

        } catch (e: Exception) {

            ResultadoCuestionarioGenerado(
                titulo = null,
                preguntas = null,
                mensaje = """
                    No se pudo realizar la conexión.

                    ${e.message ?: "Error desconocido"}
                """.trimIndent()
            )

        } finally {

            conexion?.disconnect()
        }
    }

private fun procesarJSONCuestionario(
    contenido: String,
    cantidadEsperada: Int
): ResultadoCuestionarioGenerado {

    return try {

        val json =
            JSONObject(contenido)

        val titulo =
            json.optString(
                "titulo",
                ""
            ).trim()

        val preguntasJson =
            json.optJSONArray("preguntas")

        if (titulo.isBlank()) {

            return ResultadoCuestionarioGenerado(
                titulo = null,
                preguntas = null,
                mensaje = """
                    La IA no devolvió un título válido.
                """.trimIndent()
            )
        }

        if (preguntasJson == null) {

            return ResultadoCuestionarioGenerado(
                titulo = null,
                preguntas = null,
                mensaje = """
                    El modelo respondió, pero el JSON
                    no contiene el campo "preguntas".
                """.trimIndent()
            )
        }

        if (
            preguntasJson.length() != cantidadEsperada
        ) {

            return ResultadoCuestionarioGenerado(
                titulo = null,
                preguntas = null,
                mensaje = """
                    La IA devolvió
                    ${preguntasJson.length()} preguntas,
                    pero se solicitaron
                    $cantidadEsperada.
                """.trimIndent()
            )
        }

        val preguntas =
            mutableListOf<PreguntaCuestionario>()

        for (
        indice in 0 until preguntasJson.length()
        ) {

            val preguntaJson =
                preguntasJson.getJSONObject(indice)

            val textoPregunta =
                preguntaJson.optString(
                    "pregunta",
                    ""
                ).trim()

            val opciones =
                preguntaJson.optJSONArray(
                    "opciones"
                )

            val respuestaCorrecta =
                preguntaJson.optInt(
                    "respuestaCorrecta",
                    -1
                )

            if (
                textoPregunta.isBlank() ||
                opciones == null ||
                opciones.length() != 3 ||
                respuestaCorrecta !in 0..2
            ) {

                return ResultadoCuestionarioGenerado(
                    titulo = null,
                    preguntas = null,
                    mensaje = """
                        La pregunta ${indice + 1}
                        no tiene una estructura válida.

                        Cada pregunta debe tener:
                        - texto
                        - exactamente 3 opciones
                        - una respuesta correcta entre 0 y 2.
                    """.trimIndent()
                )
            }

            val respuestas =
                mutableListOf<RespuestaCuestionario>()

            for (
            indiceOpcion in 0 until opciones.length()
            ) {

                val textoOpcion =
                    opciones
                        .optString(
                            indiceOpcion,
                            ""
                        )
                        .trim()

                if (textoOpcion.isBlank()) {

                    return ResultadoCuestionarioGenerado(
                        titulo = null,
                        preguntas = null,
                        mensaje = """
                            La pregunta ${indice + 1}
                            contiene una opción vacía.
                        """.trimIndent()
                    )
                }

                respuestas.add(
                    RespuestaCuestionario(
                        texto = textoOpcion,
                        esCorrecta =
                            indiceOpcion ==
                                    respuestaCorrecta
                    )
                )
            }

            preguntas.add(
                PreguntaCuestionario(
                    texto = textoPregunta,
                    respuestas = respuestas
                )
            )
        }

        ResultadoCuestionarioGenerado(
            titulo = titulo,
            preguntas = preguntas,
            mensaje = "Cuestionario generado correctamente."
        )

    } catch (e: Exception) {

        ResultadoCuestionarioGenerado(
            titulo = null,
            preguntas = null,
            mensaje = """
                La IA respondió, pero el contenido
                no pudo interpretarse como JSON válido.

                Respuesta recibida:

                $contenido

                Error:

                ${e.message ?: "Error desconocido"}
            """.trimIndent()
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    showSystemUi = true
)
@Composable
private fun PreviewCuestionarioRapido() {

    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
        }
    }
}