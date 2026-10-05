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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import edu.unicauca.aplimovil.studyhub_application.BuildConfig
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
    var contenido by remember { // guarda el cotnendo o el tema que escribe el usuario
        mutableStateOf("")
    }

    var cantidadPreguntas by remember { // guarda la cantidad de preguntas que escribe el usuario
        mutableStateOf("")
    }

    var mostrarErrorContenido by remember { // controla si debe mostrar un error en el campo del contenido
        mutableStateOf(false)
    }

    var mostrarErrorCantidad by remember { // lo mismo aqui en cantidad
        mutableStateOf(false)
    }

    var generando by remember { // indica si el cuestionario se esta generando
        mutableStateOf(false)
    }

    val alcanceCorutinas = rememberCoroutineScope() // ejecuto tareas que pueden tardar, en este caso a open router

    Dialog( // creo la ventana de cuestionario rapido
        onDismissRequest = { // solo se permite cerrar si no se esta generando el cuestionario
            if (!generando) {
                onCerrar()
            }
        },
        properties = DialogProperties( // defino ancho permitido por el propio diseño
            usePlatformDefaultWidth = false
        )
    ) {

        val ventana = // obtiene la ventana real del dialogo para modificar sus propiedades
            (LocalView.current.parent as? DialogWindowProvider)?.window

        SideEffect {
            ventana?.setDimAmount(0.5f)
        }

        Box(
            modifier = Modifier
                .size(
                    width = 390.dp,
                    height = 355.dp
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

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

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

                    CircularProgressIndicator( // muestra el indicador de carga mientras la IA genera el cuestionario
                        modifier = Modifier.size(30.dp),
                        color = Color(0xFFACC6FF),
                        strokeWidth = 3.dp
                    )

                } else {

                    Box( // crea el boton de generar
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

                                if ( // contenido no este vacio
                                    contenido.isNotBlank() &&
                                    cantidad != null &&
                                    cantidad > 0
                                ) {

                                    mostrarErrorCantidad = false
                                    generando = true // comienza la generacion del cuestionario

                                    alcanceCorutinas.launch { // inicia la solicitud de la IA

                                        val resultado = // enviamos contenido y cantidad de preguntas open router
                                            generarCuestionarioOpenRouter(
                                                texto = contenido,
                                                cantidadPreguntas = cantidad
                                            )

                                        if ( // comprueba que la IA haya devuelto un titulo y pregunta validas
                                            resultado.titulo != null &&
                                            resultado.preguntas != null
                                        ) {

                                            viewModel.guardarCuestionarioGenerado( // guarda en la base de datos el cuestionario generado
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
                            text = "Digite el contenido del tema aqui",
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

                    if ( // comprueba que todos los caracteres sean numeros
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
                decorationBox = { campoTexto -> // muestra el texto de ayuda cuando el campo este vacio

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
    withContext(Dispatchers.IO) { // ejecuta la conexion de red para operaciones de entrada y salida

        var conexion: HttpURLConnection? = null // guarda la conexion para utilizar y cerrarla posteriormente

        try {

            val apiKey =
                BuildConfig.OPENROUTER_API_KEY // obtenemos la API KEY

            if (apiKey.isBlank()) { // comprueba si no esta vacia

                return@withContext ResultadoCuestionarioGenerado(
                    titulo = null,
                    preguntas = null,
                    mensaje = "No se encontró la API Key de OpenRouter."
                )
            }

            val url = URL(
                "https://openrouter.ai/api/v1/chat/completions" // define la direccion de la API de open router que recibe las solicitudes
            )

            conexion =
                url.openConnection() as HttpURLConnection // abre conexion HTTP

            conexion.requestMethod = "POST"
            conexion.connectTimeout = 30000
            conexion.readTimeout = 120000

            conexion.setRequestProperty( // envio la API KEY
                "Authorization",
                "Bearer $apiKey"
            )

            conexion.setRequestProperty( // indica que datos se enviaran y que estaran en formato JSON
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

            val cuerpo = JSONObject().apply { // construye el objeto JSON que se enviara a open router

                put( // indica que modelo de IA voy a utilizar
                    "model",
                    "poolside/laguna-s-2.1:free"
                )

                put( // indica la lista de mensajes que va a recibir
                    "messages",
                    JSONArray().apply {

                        put(  // agrega un mensaje enviado por el usuario
                            JSONObject().apply {

                                put(  // indica que el mensaje le pertenece al usuario
                                    "role",
                                    "user"
                                )

                                put( // coloca las instrucciones creadas
                                    "content",
                                    instrucciones
                                )
                            }
                        )
                    }
                )
            }

            conexion.outputStream.use { salida -> // enviamos el json a open router

                salida.write(
                    cuerpo.toString().toByteArray(
                        Charsets.UTF_8
                    )
                )
            }

            val codigoRespuesta = // obtene codigo del HTTP que devolvio el servidor
                conexion.responseCode

            val flujo = // selecciona el flujo correcto segun el resultado de la solicitud
                if (codigoRespuesta in 200..299) { // si fue exitoso la lee normal o si no lee el error
                    conexion.inputStream
                } else {
                    conexion.errorStream
                }

            // lee todo el contenido

            val textoRespuesta =
                flujo
                    ?.bufferedReader()
                    ?.use {
                        it.readText()
                    }
                    ?: ""

            if (codigoRespuesta !in 200..299) { // comprueba si open router devolvio un codigo fuera del rango exitoso

                return@withContext ResultadoCuestionarioGenerado( // devuelve error si intenta preocesar la respuesta como cuestionario
                    titulo = null,
                    preguntas = null,
                    mensaje = """
                        Error HTTP $codigoRespuesta

                        $textoRespuesta
                    """.trimIndent()
                )
            }

            val respuestaApi = // convierte la respuesta completa de openrouter en un objeto JSON
                JSONObject(textoRespuesta)

            val choices = // obtene la lista de choices de la respuesta de la API
                respuestaApi.optJSONArray("choices")

            if ( // comprueba que si existe y que tenga al menos una respuesta
                choices == null ||
                choices.length() == 0
            ) {

                return@withContext ResultadoCuestionarioGenerado( // devuelve error si el modelo no produjo ninguna respuesta
                    titulo = null,
                    preguntas = null,
                    mensaje = """
                        La API respondió, pero no se encontró
                        ninguna respuesta del modelo.
                    """.trimIndent()
                )
            }

            val mensaje = // obtiene el primer elemento de choices
                choices
                    .getJSONObject(0)
                    .getJSONObject("message")

            val contenido = // obtiene el texto generado por el modelo
                mensaje
                    .optString(
                        "content",
                        ""
                    )
                    .trim()

            if (contenido.isBlank()) { // comprueba que la IA haya devuelto un conenido

                return@withContext ResultadoCuestionarioGenerado( // devuelve error si la respuesta esta vacia
                    titulo = null,
                    preguntas = null,
                    mensaje = """
                        La API respondió correctamente,
                        pero el modelo no devolvió contenido.
                    """.trimIndent()
                )
            }

            procesarJSONCuestionario( // envia el JSON generado por la IA a la funcion que lo interpreta
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

private fun procesarJSONCuestionario( // vamos a procesar el JSON recibido por la IA y lo convierte a cuestionario
    contenido: String,
    cantidadEsperada: Int
): ResultadoCuestionarioGenerado {

    return try { // intenta interpretar y valida el contenido recibido

        val json = // convierte el texto recibido en un objeto json
            JSONObject(contenido)

        val titulo = // obtiene el titulo generado por la IA
            json.optString(
                "titulo",
                ""
            ).trim()

        val preguntasJson = // obtiene la lista de preguntas del JSON
            json.optJSONArray("preguntas")

        if (titulo.isBlank()) { // comprueba si la IA genero un titulo

            return ResultadoCuestionarioGenerado(
                titulo = null,
                preguntas = null,
                mensaje = """
                    La IA no devolvió un título válido.
                """.trimIndent()
            )
        }

        if (preguntasJson == null) { // lo mismo con preguntas

            return ResultadoCuestionarioGenerado(
                titulo = null,
                preguntas = null,
                mensaje = """
                    El modelo respondió, pero el JSON
                    no contiene el campo "preguntas".
                """.trimIndent()
            )
        }

        if ( // comprueba la cantidad de pregunta sea exactamente la solicitada
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

        val preguntas = // crea una lista donde se almacenaran las preguntas ya convertidas
            mutableListOf<PreguntaCuestionario>()

        for ( // recorre todas las preguntas recibidas
        indice in 0 until preguntasJson.length()
        ) {

            val preguntaJson = // obtiene la pregunta actual como objeto JSON
                preguntasJson.getJSONObject(indice)

            val textoPregunta = // obtiene el texto de la pregunta
                preguntaJson.optString(
                    "pregunta",
                    ""
                ).trim()

            val opciones = // optiene las opcionese de respuesta
                preguntaJson.optJSONArray(
                    "opciones"
                )

            val respuestaCorrecta = // obtiene el indice de la respuesta correcta
                preguntaJson.optInt(
                    "respuestaCorrecta",
                    -1
                )

            if ( // comprueba que la pregunta tenga todos los campos necesarios
                textoPregunta.isBlank() ||
                opciones == null ||
                opciones.length() != 3 ||
                respuestaCorrecta !in 0..2
            ) {

                return ResultadoCuestionarioGenerado( // devuelve error si la estrcutura de la pregunta no es valida
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

            val respuestas = // crea una lista para almacenar las respuesta de la pregunta actual
                mutableListOf<RespuestaCuestionario>()

            for ( // recorre las 3 opciones de las preguntas
            indiceOpcion in 0 until opciones.length()
            ) {

                val textoOpcion = // obtiene el texto de la opcion actual
                    opciones
                        .optString(
                            indiceOpcion,
                            ""
                        )
                        .trim()

                if (textoOpcion.isBlank()) { // comprueba que no este vacia

                    return ResultadoCuestionarioGenerado( // devuelve error si esta vacia la opcion
                        titulo = null,
                        preguntas = null,
                        mensaje = """
                            La pregunta ${indice + 1}
                            contiene una opción vacía.
                        """.trimIndent()
                    )
                }

                respuestas.add( // agrga la opcion convertida al modelo de study hub
                    RespuestaCuestionario(
                        texto = textoOpcion,
                        esCorrecta = //  marca como correcta la opcion cuyo indice coincida con respuestaCorrecta
                            indiceOpcion ==
                                    respuestaCorrecta
                    )
                )
            }

            preguntas.add( // agrega la pregunta completa a la lista de preguntas
                PreguntaCuestionario(
                    texto = textoPregunta,
                    respuestas = respuestas
                )
            )
        }

        ResultadoCuestionarioGenerado( // devuelve el cuestionario completo despues de valdiar todas las peguntas
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
    showSystemUi = true,
    widthDp = 412,
    heightDp = 915
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

                    Text(
                        text = "Cuestionario instantaneo",
                        color = Color.White,
                        fontSize = 24.sp
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    CampoContenidoTema(
                        texto = "",
                        mostrarError = false,
                        habilitado = true,
                        onTextoCambiado = {}
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoCantidadPreguntas(
                        texto = "",
                        mostrarError = false,
                        habilitado = true,
                        onTextoCambiado = {}
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(
                                width = 107.dp,
                                height = 36.dp
                            )
                            .background(
                                color = Color(0xFFACC6FF),
                                shape = RoundedCornerShape(20.dp)
                            ),
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

