package edu.unicauca.aplimovil.studyhub_application.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Esquema de colores oscuro de StudyHub.
 *
 * Utiliza exclusivamente la paleta definida en Colores.kt.
 * No utiliza Material Theme Builder ni colores dinámicos
 * del sistema Android.
 */
private val EsquemaColoresOscuroStudyHub = darkColorScheme(
    primary = AcentoPrincipal,
    background = FondoPrincipal,
    surface = SuperficieTarjeta,
    onPrimary = IconoOscuro,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal
)

/**
 * Tema principal de StudyHub.
 *
 * Por el momento la aplicación utiliza únicamente un tema oscuro.
 * La tipografía se obtiene desde Tipografia.kt.
 */
@Composable
fun StudyHub_ApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaColoresOscuroStudyHub,
        typography = TipografiaMaterialStudyHub,
        content = content
    )
}