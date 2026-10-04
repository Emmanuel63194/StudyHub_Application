package edu.unicauca.aplimovil.studyhub_application.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import edu.unicauca.aplimovil.studyhub_application.R
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalendarioEntity
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.theme.TipografiaStudyHub
import java.util.Calendar

internal val ColorTextoSecundarioEvento = Color(0xFF989898)
internal val ColorRojoEvento = Color(0xFFE5173F)
private val ColorSuperficieNuevoEvento = Color(0xFF272727)

internal fun fechaAMillis(anio: Int, mes: Int, dia: Int): Long =
    Calendar.getInstance().apply {
        clear()
        set(anio, mes, dia)
    }.timeInMillis

internal fun formatearFechaEvento(fecha: Long): String {
    val calendario = Calendar.getInstance().apply { timeInMillis = fecha }
    return "${NombresMeses[calendario.get(Calendar.MONTH)]} " +
            "${calendario.get(Calendar.DAY_OF_MONTH)}, " +
            "${calendario.get(Calendar.YEAR)}"
}

internal fun formatearHoraEvento(minutosDelDia: Int): String {
    val hora24 = minutosDelDia / 60
    val minutos = minutosDelDia % 60
    val hora12 = if (hora24 % 12 == 0) 12 else hora24 % 12
    val sufijo = if (hora24 >= 12) "PM" else "AM"
    return "$hora12:${minutos.toString().padStart(2, '0')} $sufijo"
}

internal fun formatearFechaHoraEvento(evento: CalendarioEntity): String =
    "${formatearFechaEvento(evento.fecha)} (${formatearHoraEvento(evento.hora)})"

private fun hora12DesdeMinutos(minutosDelDia: Int): Int {
    val hora = (minutosDelDia / 60) % 12
    return if (hora == 0) 12 else hora
}

private fun minutosDesdeSelector(hora12: Int, minuto: Int, esPm: Boolean): Int =
    ((hora12 % 12) + if (esPm) 12 else 0) * 60 + minuto

@Composable
fun NuevoEvento(
    eventoAEditar: CalendarioEntity? = null,
    onCerrar: () -> Unit,
    onGuardar: (CalendarioEntity) -> Unit
) {

    if (!LocalInspectionMode.current) {
        SolicitarPermisoNotificaciones()
    }

    val administradorFoco = LocalFocusManager.current
    val contexto = LocalContext.current

    var titulo by remember { mutableStateOf(eventoAEditar?.titulo ?: "") }
    var nota by remember { mutableStateOf(eventoAEditar?.nota ?: "") }


    var fechaMillis by remember { mutableStateOf(eventoAEditar?.fecha) }

    val horaGuardada = eventoAEditar?.hora

    var hora12 by remember {
        mutableStateOf(horaGuardada?.let { hora12DesdeMinutos(it) } ?: 1)
    }
    var minuto by remember { mutableStateOf(horaGuardada?.rem(60) ?: 0) }
    var esPm by remember { mutableStateOf(horaGuardada?.let { it / 60 >= 12 } ?: false) }

    var horaEstablecida by remember { mutableStateOf(horaGuardada != null) }

    var mostrarErrores by remember { mutableStateOf(false) }

    val errorTitulo = mostrarErrores && titulo.isBlank()
    val errorFecha = mostrarErrores && fechaMillis == null
    val errorHora = mostrarErrores && !horaEstablecida

    fun cambiarHora(delta: Int) {
        hora12 = (hora12 - 1 + delta + 12) % 12 + 1
        horaEstablecida = true
    }

    fun cambiarMinuto(delta: Int) {
        minuto = (minuto + delta + 60) % 60
        horaEstablecida = true
    }

    fun abrirSelectorFecha() {
        val base = Calendar.getInstance().apply {
            fechaMillis?.let { timeInMillis = it }
        }
        DatePickerDialog(
            contexto,
            { _, anio, mes, dia -> fechaMillis = fechaAMillis(anio, mes, dia) },
            base.get(Calendar.YEAR),
            base.get(Calendar.MONTH),
            base.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun alternarAmPm() {
        esPm = !esPm
        horaEstablecida = true
    }

    fun intentarGuardar() {
        mostrarErrores = true

        if (titulo.isBlank()) titulo = ""

        val fecha = fechaMillis

        if (titulo.isNotBlank() && fecha != null && horaEstablecida) {
            administradorFoco.clearFocus()

            onGuardar(
                CalendarioEntity(
                    id = eventoAEditar?.id ?: 0L,
                    titulo = titulo.trim(),
                    fecha = fecha,
                    hora = minutosDesdeSelector(hora12, minuto, esPm),
                    nota = nota.trim()
                )
            )

            onCerrar()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            // Consume los toques para que no lleguen al fondo oscuro.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.cerrar_icono),
                contentDescription = "Cerrar",
                modifier = Modifier
                    .size(38.dp)
                    .clickable(onClick = onCerrar)
            )

            Box(
                modifier = Modifier
                    .size(width = 107.dp, height = 36.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { intentarGuardar() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (eventoAEditar == null) "Guardar" else "Editar",
                    color = Color.Black,
                    style = TipografiaStudyHub.ResaltadoTarjeta,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CampoTextoEvento(
            valor = titulo,
            onCambio = { titulo = it },
            textoGuia = "Agregar titulo",
            mensajeError = if (errorTitulo) "Falta agregar titulo" else null,
            unaLinea = true,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
        )

        LineaDivisoriaEvento()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { abrirSelectorFecha() }
                .padding(horizontal = 4.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.calendario_icono),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = fechaMillis?.let { formatearFechaEvento(it) }
                    ?: "Establecer dia del evento",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ">",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp
            )
        }

        if (errorFecha) {
            MensajeErrorEvento(
                texto = "Falta el dia del evento",
                tamano = 14.sp,
                modifier = Modifier.padding(start = 46.dp, bottom = 8.dp)
            )
        }

        LineaDivisoriaEvento()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 14.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.reloj_icono),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Establecer hora del evento",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
        }

        if (errorHora) {
            MensajeErrorEvento(
                texto = "Falta la hora del evento",
                tamano = 14.sp,
                modifier = Modifier.padding(start = 46.dp, bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SelectorValor(
                texto = hora12.toString().padStart(2, '0'),
                onMenos = { cambiarHora(-1) },
                onMas = { cambiarHora(1) }
            )

            SeparadorSelector()

            SelectorValor(
                texto = minuto.toString().padStart(2, '0'),
                onMenos = { cambiarMinuto(-1) },
                onMas = { cambiarMinuto(1) }
            )

            SeparadorSelector()

            SelectorValor(
                texto = if (esPm) "PM" else "AM",
                onMenos = { alternarAmPm() },
                onMas = { alternarAmPm() }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LineaDivisoriaEvento()

        /* ---- Nota ---- */
        CampoTextoEvento(
            valor = nota,
            onCambio = { nota = it },
            textoGuia = "Agregar nota",
            unaLinea = false,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
        )
    }
}

@Composable
internal fun LineaDivisoriaEvento() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White)
    )
}

@Composable
private fun MensajeErrorEvento(
    texto: String,
    tamano: TextUnit,
    modifier: Modifier = Modifier
) {
    Text(
        text = texto,
        color = ColorRojoEvento,
        fontSize = tamano,
        modifier = modifier
    )
}

@Composable
private fun CampoTextoEvento(
    valor: String,
    onCambio: (String) -> Unit,
    textoGuia: String,
    unaLinea: Boolean,
    modifier: Modifier = Modifier,
    mensajeError: String? = null
) {

    BasicTextField(
        value = valor,
        onValueChange = onCambio,
        singleLine = unaLinea,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        ),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { campo ->
            Box {
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

@Composable
private fun SelectorValor(
    texto: String,
    onMenos: () -> Unit,
    onMas: () -> Unit
) {

    Row(
        modifier = Modifier
            .size(width = 101.dp, height = 25.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {

        BotonSelector(simbolo = "−", onClick = onMenos)

        Text(
            text = texto,
            color = ColorTextoSecundarioEvento,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        BotonSelector(simbolo = "+", onClick = onMas)
    }
}

@Composable
private fun BotonSelector(
    simbolo: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(30.dp)
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
        fontSize = 20.sp
    )
}

@Composable
private fun SolicitarPermisoNotificaciones() {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val contexto = LocalContext.current

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        val concedido = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!concedido) {
            lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF111318,
    widthDp = 412,
    heightDp = 800
)
@Composable
fun NuevoEventoPreview() {

    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.BottomCenter
        ) {
            NuevoEvento(
                onCerrar = {},
                onGuardar = {}
            )
        }
    }
}