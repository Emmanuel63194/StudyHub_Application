package edu.unicauca.aplimovil.studyhub_application.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tipografía centralizada de StudyHub.
 *
 * Utiliza la familia sans-serif del sistema, proporcionando
 * una apariencia limpia, moderna y legible.
 */
private val FamiliaTipograficaStudyHub = FontFamily.SansSerif

/**
 * Estilos tipográficos específicos de StudyHub.
 */
object TipografiaStudyHub {

    // "Resumen", "Hoy" y "Mañana"
    val TituloSeccion = TextStyle(
        fontFamily = FamiliaTipograficaStudyHub,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    )

    // "Eventos pendientes"
    val ResaltadoTarjeta = TextStyle(
        fontFamily = FamiliaTipograficaStudyHub,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    )

    // Mensaje de estado vacío
    val MensajeEstadoVacio = TextStyle(
        fontFamily = FamiliaTipograficaStudyHub,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    )
}

/**
 * Configuración tipográfica base de Material3.
 *
 * Se mantiene para que MaterialTheme pueda funcionar correctamente.
 * Los estilos específicos de StudyHub se encuentran en
 * TipografiaStudyHub.
 */
val TipografiaMaterialStudyHub = Typography()