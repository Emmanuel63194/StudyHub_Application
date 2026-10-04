package edu.unicauca.aplimovil.studyhub_application.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.viewmodel.AsignaturaViewModel
import java.text.Normalizer

@Composable
fun CrearAsignatura(
    viewModel: AsignaturaViewModel,
    asignaturaAEditar: AsignaturaEntity? = null,
    onBack: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf(asignaturaAEditar?.nombre ?: "") }
    var salon by remember { mutableStateOf(asignaturaAEditar?.salon ?: "") }
    var bloques by remember {
        mutableStateOf(
            textoABloques(asignaturaAEditar?.horario ?: "")
        )
    }
    var errorNombre by remember { mutableStateOf(false) }

    fun guardar() {
        if (nombre.isBlank()) {
            nombre = ""
        }

        errorNombre = nombre.isEmpty()

        if (errorNombre) return

        val horarioTexto = bloquesATexto(bloques)

        if (asignaturaAEditar == null) {
            viewModel.agregar(
                nombre.trim(),
                salon.trim(),
                horarioTexto
            )
        } else {
            viewModel.editar(
                asignaturaAEditar.copy(
                    nombre = nombre.trim(),
                    salon = salon.trim(),
                    horario = horarioTexto
                )
            )
        }

        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.regresar_icono),
                contentDescription = "Regresar",
                modifier = Modifier
                    .size(35.dp)
                    .clickable(onClick = onBack)
            )

            Box(
                modifier = Modifier
                    .size(width = 107.dp, height = 36.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { guardar() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (asignaturaAEditar == null) "Agregar" else "Editar",
                    color = Color.Black,
                    style = TipografiaStudyHub.ResaltadoTarjeta,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CampoTextoAsignatura(
            valor = nombre,
            onCambio = {
                nombre = it
                errorNombre = false
            },
            textoGuia = "Añadir nombre",
            icono = R.drawable.asignatura_icono,
            mensajeError = if (errorNombre) {
                "Falta el nombre de la asignatura"
            } else {
                null
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        CampoTextoAsignatura(
            valor = salon,
            onCambio = { salon = it },
            textoGuia = "Agregar salon",
            icono = R.drawable.ubicacion_blanco_icono
        )

        Spacer(modifier = Modifier.height(30.dp))

        ContenedorHorario(
            bloques = bloques,
            onCambio = { bloques = it }
        )
    }
}

@Composable
private fun CampoTextoAsignatura(
    valor: String,
    onCambio: (String) -> Unit,
    textoGuia: String,
    icono: Int,
    mensajeError: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(67.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icono),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        BasicTextField(
            value = valor,
            onValueChange = onCambio,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { campo ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (valor.isEmpty()) {
                        Text(
                            text = mensajeError ?: textoGuia,
                            color = if (mensajeError != null) {
                                ColorRojoEvento
                            } else {
                                ColorTextoSecundarioEvento
                            },
                            fontSize = 20.sp
                        )
                    }

                    campo()
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContenedorHorario(
    bloques: List<BloqueDia>,
    onCambio: (List<BloqueDia>) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }

    fun alternarDia(dia: String) {
        if (bloques.any { it.dia == dia }) {
            onCambio(
                bloques.filter { it.dia != dia }
            )
        } else {
            onCambio(
                (bloques + BloqueDia(
                    dia = dia,
                    inicio = 7 * 60,
                    fin = 9 * 60
                )).sortedBy {
                    DiasHorario.indexOf(it.dia)
                }
            )
        }
    }

    fun cambiarBloque(nuevo: BloqueDia) {
        onCambio(
            bloques.map {
                if (it.dia == nuevo.dia) nuevo else it
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(67.dp)
                .clickable { abierto = !abierto }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.horario_blanco_icono),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Establecer horario",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ">",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.rotate(
                    if (abierto) 90f else 0f
                )
            )
        }

        if (abierto) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                LineaDivisoriaEvento()

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        6.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DiasHorario.take(4).forEach { dia ->
                        ChipDia(
                            dia = dia,
                            seleccionado = bloques.any {
                                it.dia == dia
                            },
                            onClick = {
                                alternarDia(dia)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        6.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DiasHorario.drop(4).forEach { dia ->
                        ChipDia(
                            dia = dia,
                            seleccionado = bloques.any {
                                it.dia == dia
                            },
                            onClick = {
                                alternarDia(dia)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LineaDivisoriaEvento()

                bloques.forEach { bloque ->

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = bloque.dia,
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FilaHora(
                        titulo = "Inicio",
                        minutos = bloque.inicio,
                        onCambio = {
                            cambiarBloque(
                                bloque.copy(inicio = it)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FilaHora(
                        titulo = "Final",
                        minutos = bloque.fin,
                        onCambio = {
                            cambiarBloque(
                                bloque.copy(fin = it)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipDia(
    dia: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(29.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(
                if (seleccionado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (seleccionado) {
            Text(
                text = dia,
                color = Color.Black,
                style = TipografiaStudyHub.ResaltadoTarjeta,
                fontSize = 17.sp,
                maxLines = 1
            )
        } else {
            Text(
                text = dia,
                color = Color.White,
                fontSize = 17.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FilaHora(
    titulo: String,
    minutos: Int,
    onCambio: (Int) -> Unit
) {
    val hora24 = minutos / 60
    val hora12 = if (hora24 % 12 == 0) {
        12
    } else {
        hora24 % 12
    }

    val minuto = minutos % 60

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = ColorTextoSecundarioEvento,
            fontSize = 20.sp,
            maxLines = 1,
            modifier = Modifier.width(62.dp)
        )

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CeldaSelector(
                texto = hora12.toString().padStart(2, '0'),
                onMenos = {
                    onCambio(
                        cambiarHoraDia(minutos, -1)
                    )
                },
                onMas = {
                    onCambio(
                        cambiarHoraDia(minutos, 1)
                    )
                }
            )

            SeparadorSelector()

            CeldaSelector(
                texto = minuto.toString().padStart(2, '0'),
                onMenos = {
                    onCambio(
                        cambiarMinutoDia(minutos, -5)
                    )
                },
                onMas = {
                    onCambio(
                        cambiarMinutoDia(minutos, 5)
                    )
                }
            )

            SeparadorSelector()

            CeldaSelector(
                texto = if (hora24 >= 12) "PM" else "AM",
                onMenos = {
                    onCambio(
                        alternarAmPmDia(minutos)
                    )
                },
                onMas = {
                    onCambio(
                        alternarAmPmDia(minutos)
                    )
                }
            )
        }
    }
}

@Composable
private fun RowScope.CeldaSelector(
    texto: String,
    onMenos: () -> Unit,
    onMas: () -> Unit
) {
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
    ) {
        SelectorValor(
            texto = texto,
            onMenos = onMenos,
            onMas = onMas,
            modifier = Modifier
                .widthIn(max = 101.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun SelectorValor(
    texto: String,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .height(25.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White)
    ) {
        val compacto = maxWidth < 90.dp

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotonSelector(
                simbolo = "−",
                ancho = if (compacto) 20.dp else 30.dp,
                onClick = onMenos
            )

            Text(
                text = texto,
                color = ColorTextoSecundarioEvento,
                fontSize = if (compacto) 15.sp else 18.sp,
                lineHeight = if (compacto) 18.sp else 20.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.weight(1f)
            )

            BotonSelector(
                simbolo = "+",
                ancho = if (compacto) 20.dp else 30.dp,
                onClick = onMas
            )
        }
    }
}

@Composable
private fun BotonSelector(
    simbolo: String,
    ancho: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(ancho)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = simbolo,
            color = Color.Black,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SeparadorSelector() {
    Text(
        text = "-",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 20.sp,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

internal data class BloqueDia(
    val dia: String,
    val inicio: Int,
    val fin: Int
)

private val DiasHorario = listOf(
    "Lunes",
    "Martes",
    "Miércoles",
    "Jueves",
    "Viernes",
    "Sábado",
    "Domingo"
)

private fun sinTildes(texto: String): String =
    Normalizer
        .normalize(texto, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")

private fun bloquesATexto(
    bloques: List<BloqueDia>
): String =
    bloques.joinToString(";") {
        "${it.dia},${it.inicio},${it.fin}"
    }

internal fun textoABloques(
    texto: String
): List<BloqueDia> {
    if (texto.isBlank()) {
        return emptyList()
    }

    return texto
        .split(";")
        .mapNotNull { parte ->

            val datos = parte.split(",")

            if (datos.size != 3) {
                return@mapNotNull null
            }

            val dia = DiasHorario.firstOrNull {
                sinTildes(it).equals(
                    sinTildes(datos[0].trim()),
                    ignoreCase = true
                )
            } ?: return@mapNotNull null

            val inicio =
                datos[1].trim().toIntOrNull()
                    ?: return@mapNotNull null

            val fin =
                datos[2].trim().toIntOrNull()
                    ?: return@mapNotNull null

            BloqueDia(
                dia = dia,
                inicio = inicio,
                fin = fin
            )
        }
        .sortedBy {
            DiasHorario.indexOf(it.dia)
        }
}

private fun cambiarHoraDia(
    minutos: Int,
    cambio: Int
): Int {
    val hora24 = minutos / 60
    val esPm = hora24 >= 12

    val hora12 =
        (hora24 % 12 + cambio + 12) % 12

    return (
            hora12 +
                    if (esPm) 12 else 0
            ) * 60 + minutos % 60
}

private fun cambiarMinutoDia(
    minutos: Int,
    cambio: Int
): Int {
    val minuto =
        (minutos % 60 + cambio + 60) % 60

    return (minutos / 60) * 60 + minuto
}

private fun alternarAmPmDia(
    minutos: Int
): Int =
    (minutos + 12 * 60) % (24 * 60)

@Composable
fun CampoTexto(
    valor: String,
    onChange: (String) -> Unit,
    placeholder: String,
    icono: Int,
    hayError: Boolean = false,
    textoError: String = "",
    teclado: KeyboardType = KeyboardType.Text
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(67.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icono),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        BasicTextField(
            value = valor,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = teclado
            ),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp
            ),
            cursorBrush = SolidColor(
                MaterialTheme.colorScheme.onBackground
            ),
            decorationBox = { campo ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (valor.isEmpty()) {
                        Text(
                            text = if (hayError) {
                                textoError
                            } else {
                                placeholder
                            },
                            color = if (hayError) {
                                Color.Red
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontSize = 18.sp
                        )
                    }

                    campo()
                }
            }
        )
    }
}

@Composable
fun BarraSuperiorFormulario(
    onBack: () -> Unit,
    onAgregar: () -> Unit,
    textoBoton: String = "Agregar"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.regresar_icono),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable { onBack() }
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onAgregar() }
                .padding(
                    horizontal = 26.dp,
                    vertical = 8.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = textoBoton,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp
            )
        }
    }
}