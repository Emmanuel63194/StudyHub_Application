package edu.unicauca.aplimovil.studyhub_application.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalificacionEntity
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import edu.unicauca.aplimovil.studyhub_application.viewmodel.AsignaturaViewModel
import edu.unicauca.aplimovil.studyhub_application.viewmodel.CalificacionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val DIA_MS = 24L * 60 * 60 * 1000

private val soloDecimal = Regex("([0-9]+\\.?[0-9]*)?")
private val soloEntero = Regex("[0-9]*")

// #ACC6FF: botón Agregar, asignatura elegida y botón +
private val AzulCalificacion = Color(0xFFACC6FF)

@Composable
fun CrearCalificacion(
    calificacionViewModel: CalificacionViewModel,
    asignaturaViewModel: AsignaturaViewModel,
    onBack: () -> Unit = {},
    onCrearAsignatura: () -> Unit = {},
    calificacionAEditar: CalificacionEntity? = null
) {
    val asignaturas by asignaturaViewModel.asignaturas.collectAsState()
    val todas by calificacionViewModel.calificaciones.collectAsState()

    val contexto = LocalContext.current

    var nota by remember {
        mutableStateOf(
            calificacionAEditar?.nota?.toString() ?: ""
        )
    }

    var asignaturaId by remember {
        mutableStateOf(
            calificacionAEditar?.asignaturaId
        )
    }

    var fecha by remember {
        mutableStateOf(
            calificacionAEditar?.fecha
        )
    }

    var porcentaje by remember {
        mutableStateOf(
            calificacionAEditar?.porcentaje?.toString() ?: ""
        )
    }

    var descripcion by remember {
        mutableStateOf(
            calificacionAEditar?.descripcion ?: ""
        )
    }

    var mensajeNota by remember {
        mutableStateOf("")
    }

    var mensajePorcentaje by remember {
        mutableStateOf("")
    }

    var intento by remember {
        mutableStateOf(false)
    }

    val usado = todas
        .filter {
            it.asignaturaId == asignaturaId &&
                    it.id != calificacionAEditar?.id
        }
        .sumOf {
            it.porcentaje
        }

    val disponible = 100 - usado

    val errorNota = when {
        mensajeNota.isNotEmpty() -> mensajeNota
        intento && nota.isEmpty() -> "Falta calificación"
        else -> ""
    }

    val errorPorcentaje = when {
        mensajePorcentaje.isNotEmpty() -> mensajePorcentaje
        intento && porcentaje.isEmpty() ->
            "Falta porcentaje de calificacion"
        else -> ""
    }

    fun abrirSelectorFecha() {
        val utc = TimeZone.getTimeZone("UTC")

        val base = Calendar.getInstance(utc).apply {
            timeInMillis = fecha ?: hoyMillisCalificacion()
        }

        DatePickerDialog(
            contexto,
            { _, anio, mes, dia ->
                fecha = Calendar.getInstance(utc).apply {
                    clear()
                    set(anio, mes, dia)
                }.timeInMillis
            },
            base.get(Calendar.YEAR),
            base.get(Calendar.MONTH),
            base.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun guardar() {
        intento = true

        if (descripcion.isBlank()) {
            descripcion = ""
        }

        val notaNumero = nota.toDoubleOrNull()
        val porcentajeNumero = porcentaje.toIntOrNull()
        val idAsignatura = asignaturaId

        if (
            notaNumero == null ||
            porcentajeNumero == null ||
            idAsignatura == null
        ) {
            return
        }

        if (descripcion.isEmpty() || disponible <= 0) {
            return
        }

        if (notaNumero < 0.0 || notaNumero > 5.0) {
            nota = ""
            mensajeNota = "La calificación va de 0 a 5"
            return
        }

        if (porcentajeNumero > disponible) {
            porcentaje = ""
            mensajePorcentaje = "Máximo $disponible%"
            return
        }

        val fechaFinal = fecha ?: hoyMillisCalificacion()

        if (calificacionAEditar == null) {
            calificacionViewModel.agregar(
                idAsignatura,
                notaNumero,
                porcentajeNumero,
                descripcion.trim(),
                fechaFinal
            )
        } else {
            calificacionViewModel.editar(
                calificacionAEditar.copy(
                    asignaturaId = idAsignatura,
                    nota = notaNumero,
                    porcentaje = porcentajeNumero,
                    descripcion = descripcion.trim(),
                    fecha = fechaFinal
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BarraSuperiorCalificacion(
            onBack = onBack,
            textoBoton =
                if (calificacionAEditar == null) {
                    "Agregar"
                } else {
                    "Editar"
                },
            onAgregar = {
                guardar()
            }
        )

        Spacer(modifier = Modifier.height(5.dp))

        CampoEntradaCalificacion(
            valor = nota,
            onChange = { texto ->
                if (!texto.matches(soloDecimal)) {
                    nota = ""
                    mensajeNota = "Solo se admite números"
                } else if (
                    (texto.toDoubleOrNull() ?: 0.0) > 5.0
                ) {
                    nota = ""
                    mensajeNota = "La calificación va de 0 a 5"
                } else {
                    nota = texto
                    mensajeNota = ""
                }
            },
            placeholder = "Agregar calificacion",
            tamanoTexto = 20.sp,
            icono = R.drawable.calificacion_icono,
            hayError = errorNota.isNotEmpty(),
            textoError = errorNota,
            teclado = KeyboardType.Decimal
        )

        SelectorAsignatura(
            asignaturas = asignaturas,
            seleccionadaId = asignaturaId,
            hayError = intento && asignaturaId == null,
            onElegir = {
                asignaturaId = it
            },
            onCrearAsignatura = onCrearAsignatura
        )

        FilaEstablecerFecha(
            fecha = fecha,
            onClick = {
                abrirSelectorFecha()
            }
        )

        Text(
            text =
                "Nota: El porcentaje de calificación va de 0 a 100%. " +
                        "Al agregar calificaciones, el porcentaje disponible disminuirá.",
            color = Color.White,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        CampoEntradaCalificacion(
            valor = porcentaje,
            onChange = { texto ->
                if (!texto.matches(soloEntero)) {
                    porcentaje = ""
                    mensajePorcentaje = "Solo se admite números"
                } else if (
                    texto.length > 3 ||
                    (texto.toIntOrNull() ?: 0) > disponible
                ) {
                    porcentaje = ""
                    mensajePorcentaje = "Máximo $disponible%"
                } else {
                    porcentaje = texto
                    mensajePorcentaje = ""
                }
            },
            placeholder =
                "porcentaje de calificacion (Disponible $disponible%)",
            tamanoTexto = 14.sp,
            icono = R.drawable.calculadora,
            hayError = errorPorcentaje.isNotEmpty(),
            textoError = errorPorcentaje,
            teclado = KeyboardType.Number
        )

        CampoDescripcion(
            valor = descripcion,
            onChange = {
                descripcion = it
            },
            hayError = intento && descripcion.isBlank()
        )

        if (
            intento &&
            asignaturaId != null &&
            disponible <= 0
        ) {
            Text(
                text =
                    "No hay porcentaje disponible para agregar calificaciones",
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BarraSuperiorCalificacion(
    onBack: () -> Unit,
    textoBoton: String,
    onAgregar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.regresar_icono),
            contentDescription = "Regresar",
            modifier = Modifier
                .size(34.dp)
                .clickable { onBack() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(width = 107.dp, height = 36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AzulCalificacion)
                .clickable { onAgregar() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = textoBoton,
                style = TipografiaStudyHub.ResaltadoTarjeta,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CampoEntradaCalificacion(
    valor: String,
    onChange: (String) -> Unit,
    placeholder: String,
    tamanoTexto: androidx.compose.ui.unit.TextUnit,
    icono: Int,
    hayError: Boolean,
    textoError: String,
    teclado: KeyboardType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(69.dp)
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
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = tamanoTexto
            ),
            cursorBrush = SolidColor(
                MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            decorationBox = { campo ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (valor.isEmpty()) {
                        Text(
                            text = if (hayError) textoError else placeholder,
                            color =
                                if (hayError) {
                                    Color.Red
                                } else {
                                    ColorTextoSecundarioEvento
                                },
                            fontSize = tamanoTexto,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    campo()
                }
            }
        )
    }
}

@Composable
private fun SelectorAsignatura(
    asignaturas: List<AsignaturaEntity>,
    seleccionadaId: Int?,
    hayError: Boolean,
    onElegir: (Int) -> Unit,
    onCrearAsignatura: () -> Unit
) {
    var abierto by remember {
        mutableStateOf(false)
    }

    // El contenedor aumenta o disminuye su altura suavemente
    // cuando se abre o se cierra.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .animateContentSize()
    ) {
        // Primera fila (69 dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(69.dp)
                .clickable {
                    abierto = !abierto
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.asignatura_icono
                ),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Seleccionar una asignatura",
                color = Color.White,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f)
            )

            // Indicador ">" que gira al abrir
            Text(
                text = ">",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.rotate(
                    if (abierto) {
                        90f
                    } else {
                        0f
                    }
                )
            )
        }

        if (hayError) {
            Text(
                text = "Falta asignatura",
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        if (abierto) {

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(Color.White)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (asignaturas.isEmpty()) {
                    Text(
                        text = "Sin asignaturas (agrega una asignatura)",
                        color = ColorTextoSecundarioEvento,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BotonMasCalificacion(
                        onClick = onCrearAsignatura
                    )
                } else {
                    asignaturas.forEach { asignatura ->
                        val elegida =
                            asignatura.id == seleccionadaId

                        Box(
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .size(width = 229.dp, height = 29.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    if (elegida) {
                                        AzulCalificacion
                                    } else {
                                        Color.White
                                    }
                                )
                                .clickable {
                                    onElegir(asignatura.id)

                                    abierto = false
                                }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = asignatura.nombre,
                                color = Color.Black,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaEstablecerFecha(
    fecha: Long?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(69.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.calendario_blanco
            ),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text =
                if (fecha == null) {
                    "Establecer dia de la calificacion"
                } else {
                    formatearFechaCalificacion(fecha)
                },
            color = Color.White,
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = ">",
            color = Color.White,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun BotonMasCalificacion(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(49.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AzulCalificacion)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 20.dp, height = 2.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 20.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
private fun CampoDescripcion(
    valor: String,
    onChange: (String) -> Unit,
    hayError: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 116.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        BasicTextField(
            value = valor,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            ),
            cursorBrush = SolidColor(
                MaterialTheme.colorScheme.onBackground
            ),
            decorationBox = { campo ->
                Box {
                    if (valor.isEmpty()) {
                        Text(
                            text =
                                if (hayError) {
                                    "Falta agregar nota"
                                } else {
                                    "Agregar nota"
                                },
                            color =
                                if (hayError) {
                                    Color.Red
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

private fun hoyMillisCalificacion(): Long {
    val ahora = System.currentTimeMillis()
    val desfase = TimeZone.getDefault().getOffset(ahora)

    return (ahora + desfase) / DIA_MS * DIA_MS
}

private fun formatearFechaCalificacion(
    millis: Long
): String {
    val formato = SimpleDateFormat(
        "MMMM d, yyyy",
        Locale.forLanguageTag("es-CO")
    )

    formato.timeZone = TimeZone.getTimeZone("UTC")

    return formato
        .format(Date(millis))
        .replaceFirstChar {
            it.uppercase()
        }
}
