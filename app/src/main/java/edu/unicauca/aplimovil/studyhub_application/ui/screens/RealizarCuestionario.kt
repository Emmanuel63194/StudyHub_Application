package edu.unicauca.aplimovil.studyhub_application.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.PreguntaCuestionario
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.RespuestaCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub

private val ColorIncorrecta = Color.Red
private val ColorCorrecta = Color(0xFF16B21F)

@Composable
fun RealizarCuestionario(
    cuestionario: CuestionarioEntity?,
    onVolver: () -> Unit,
    respuestasIniciales: Map<Int, Int> = emptyMap()
) {
    if (cuestionario == null) { // aqui se define una pantalla de error cuando no se encuentra el cuestionario pero generalmente no sucede
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            BarraSuperiorRealizar(
                titulo = "Cuestionario",
                onVolver = onVolver,
                style = TipografiaStudyHub.TituloSeccion
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No se encontró el cuestionario",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Box(
                    modifier = Modifier
                        .size(
                            width = 107.dp,
                            height = 36.dp
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onVolver),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Volver",
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                }
            }
        }

        return
    }

    val seleccionadas = remember(cuestionario.id) { // guarda la respuestas seleccionadas
        mutableStateMapOf<Int, Int>().apply {
            putAll(respuestasIniciales)
        }
    }

    val preguntas = cuestionario.preguntas // obtiene todas las preguntas

    val total = preguntas.size // cuenta cuantas hay

    val respondidas = seleccionadas.size // cuenta cuantas ya tienen respuesta

    val aciertos = seleccionadas.count { (indicePregunta, indiceRespuesta) -> // determina cuantas son correctas
        preguntas
            .getOrNull(indicePregunta)
            ?.respuestas
            ?.getOrNull(indiceRespuesta)
            ?.esCorrecta == true
    }

    val terminado = total > 0 && respondidas == total // determina si todas las preguntas fueron respondidas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {

        BarraSuperiorRealizar(
            titulo = cuestionario.titulo,
            onVolver = onVolver,
            style = TipografiaStudyHub.TituloSeccion
        )

        if (preguntas.isEmpty()) { // comprueba si el cuestionario no tiene ninguna respuesta

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Este cuestionario no tiene preguntas",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }

        } else {

            LazyColumn( // muestra lista de preguntas que se pueden deslizar verticalmente
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .navigationBarsPadding(),

                contentPadding = PaddingValues( // agrega espacio alrededor del contenido de la lista
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                )
            ) {

                item { // agrega espacio antes de mostrar la primera pregunta
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                }

                itemsIndexed( // recorre todas las preguntas y muestra una por una
                    preguntas
                ) { indicePregunta, pregunta ->

                    BloquePreguntaRealizar( // muestra la pregunta y sus respuesta
                        indice = indicePregunta,
                        pregunta = pregunta,
                        elegida = seleccionadas[indicePregunta],

                        alElegir = { indiceRespuesta -> // se jecuta cuando se selecciona una respuesta

                            if (!seleccionadas.containsKey(indicePregunta)) { // evita que el usuario pueda cambiar su respuesta despues de elegirla
                                seleccionadas[indicePregunta] =
                                    indiceRespuesta
                            }
                        }
                    )

                    if (indicePregunta < preguntas.lastIndex) { // agrega una espacio entra la pregunta y la seguiente
                        Spacer(
                            modifier = Modifier.height(40.dp)
                        )
                    }
                }

                if (terminado) { // comprueba si todas ya fueron respondidas

                    item( // muestra los aciertos y el boton para reiniciarlo
                        key = "resultado"
                    ) {
                        ResultadoFinal(
                            aciertos = aciertos,
                            total = total,
                            onReiniciar = {
                                seleccionadas.clear()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraSuperiorRealizar( // este para es crer la barra superior del cuestionario
    titulo: String,
    onVolver: () -> Unit,
    style: TextStyle
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
                .clickable(onClick = onVolver)
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            text = titulo,
            style = style.copy(
                fontSize = 22.sp
            ),
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BloquePreguntaRealizar( // se muestra la pregunta del cuestionario junto con sus respuestas
    indice: Int,
    pregunta: PreguntaCuestionario,
    elegida: Int?,
    alElegir: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text( // para ver el numero de la pregunta
            text = "Pregunta ${indice + 1}",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        ContenedorTexto(
            texto = pregunta.texto,
            fondo = MaterialTheme.colorScheme.surfaceContainer
        )

        pregunta.respuestas.forEachIndexed { indiceRespuesta, respuesta -> // recorre todas las respuestas de la pregunta

            val respondida = elegida != null

            val esLaElegida =
                elegida == indiceRespuesta

            val fondo = when { //determina el color de la respuesta
                esLaElegida && respuesta.esCorrecta ->
                    ColorCorrecta

                esLaElegida ->
                    ColorIncorrecta

                else ->
                    MaterialTheme.colorScheme.surfaceContainer
            }

            val mostrarCheck = // determina si se debe mostrar el check en la respuesta correcta
                respondida &&
                        !esLaElegida &&
                        respuesta.esCorrecta &&
                        pregunta.respuestas
                            .getOrNull(elegida!!)
                            ?.esCorrecta != true

            ContenedorTexto( // muestra cada respuesta y controla si puede ser seleccionada
                texto = respuesta.texto,
                fondo = fondo,
                mostrarCheck = mostrarCheck,

                onClick = if (respondida) { // si ya respondio no permite volver a seleccionar
                    null
                } else {
                    {
                        alElegir(indiceRespuesta) // aqui la guarda
                    }
                }
            )
        }
    }
}

@Composable
private fun ContenedorTexto( // este contenedor es para mostrar el texto de la pregunta o respuesta
    texto: String,
    fondo: Color,
    mostrarCheck: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val base = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(fondo)

    Row(
        modifier = ( // aqui determinamos si ese contenedor puede ser interactivo
                if (onClick != null) {
                    base.clickable(onClick = onClick)
                } else {
                    base
                }
                ).padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = texto,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )

        if (mostrarCheck) { // aqui es para comprobar si se debe mostrar el icono de la resppuesta correcta

            Icon(
                painter = painterResource(
                    id = R.drawable.check_icono
                ),
                contentDescription = "Respuesta correcta",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(30.dp)
            )
        }
    }
}

@Composable
private fun ResultadoFinal( // mostrar el resultado final del cuestionario y se permite reiniciarlo
    aciertos: Int,
    total: Int,
    onReiniciar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "$aciertos / $total Respuestas correctas",
            color = Color.White,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(
                    width = 107.dp,
                    height = 36.dp
                )
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onReiniciar),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Reiniciar",
                style = TipografiaStudyHub.ResaltadoTarjeta,
                color = Color.Black
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF191919,
    showSystemUi = true
)
@Composable
private fun PreviewRealizarCuestionario() {

    val cuestionario = CuestionarioEntity(
        id = 1L,
        titulo = "Cuestionario 1",
        preguntas = listOf(

            PreguntaCuestionario(
                texto = "¿Qué establece la ley de Coulomb?",
                respuestas = listOf(
                    RespuestaCuestionario(
                        texto = "La fuerza entre dos cargas eléctricas",
                        esCorrecta = true
                    ),
                    RespuestaCuestionario(
                        texto = "La resistencia de un conductor",
                        esCorrecta = false
                    ),
                    RespuestaCuestionario(
                        texto = "La potencia de un circuito",
                        esCorrecta = false
                    )
                )
            ),

            PreguntaCuestionario(
                texto = "¿Cuál es la unidad del campo eléctrico?",
                respuestas = listOf(
                    RespuestaCuestionario(
                        texto = "Newton por coulomb",
                        esCorrecta = true
                    ),
                    RespuestaCuestionario(
                        texto = "Joule",
                        esCorrecta = false
                    ),
                    RespuestaCuestionario(
                        texto = "Ohm",
                        esCorrecta = false
                    )
                )
            )
        )
    )

    RealizarCuestionario(
        cuestionario = cuestionario,
        onVolver = {},

        respuestasIniciales = mapOf(
            0 to 0,
            1 to 0
        )
    )
}
