package edu.unicauca.aplimovil.studyhub_application.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val FamiliaTipograficaStudyHub = FontFamily.SansSerif

object TipografiaStudyHub {

    val TituloSeccion = TextStyle(
        fontFamily = FamiliaTipograficaStudyHub,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    )

    val ResaltadoTarjeta = TextStyle(
        fontFamily = FamiliaTipograficaStudyHub,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    )

    val MensajeEstadoVacio = TextStyle(
        fontFamily = FamiliaTipograficaStudyHub,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    )
}

private val TipografiaBaseM3 = Typography()

val TipografiaMaterialStudyHub = Typography(
    displayLarge = TipografiaBaseM3.displayLarge.copy(fontFamily = FamiliaTipograficaStudyHub),
    displayMedium = TipografiaBaseM3.displayMedium.copy(fontFamily = FamiliaTipograficaStudyHub),
    displaySmall = TipografiaBaseM3.displaySmall.copy(fontFamily = FamiliaTipograficaStudyHub),
    headlineLarge = TipografiaBaseM3.headlineLarge.copy(fontFamily = FamiliaTipograficaStudyHub),
    headlineMedium = TipografiaBaseM3.headlineMedium.copy(fontFamily = FamiliaTipograficaStudyHub),
    headlineSmall = TipografiaBaseM3.headlineSmall.copy(fontFamily = FamiliaTipograficaStudyHub),
    titleLarge = TipografiaBaseM3.titleLarge.copy(fontFamily = FamiliaTipograficaStudyHub),
    titleMedium = TipografiaBaseM3.titleMedium.copy(fontFamily = FamiliaTipograficaStudyHub),
    titleSmall = TipografiaBaseM3.titleSmall.copy(fontFamily = FamiliaTipograficaStudyHub),
    bodyLarge = TipografiaBaseM3.bodyLarge.copy(fontFamily = FamiliaTipograficaStudyHub),
    bodyMedium = TipografiaBaseM3.bodyMedium.copy(fontFamily = FamiliaTipograficaStudyHub),
    bodySmall = TipografiaBaseM3.bodySmall.copy(fontFamily = FamiliaTipograficaStudyHub),
    labelLarge = TipografiaBaseM3.labelLarge.copy(fontFamily = FamiliaTipograficaStudyHub),
    labelMedium = TipografiaBaseM3.labelMedium.copy(fontFamily = FamiliaTipograficaStudyHub),
    labelSmall = TipografiaBaseM3.labelSmall.copy(fontFamily = FamiliaTipograficaStudyHub)
)