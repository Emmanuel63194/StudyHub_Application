package edu.unicauca.aplimovil.studyhub_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.unicauca.aplimovil.studyhub_application.ui.components.PanelNavegacionLateral
import edu.unicauca.aplimovil.studyhub_application.ui.components.PantallaSeleccionada
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaAsignaturas
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCalendario
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCalificaciones
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCuestionarios
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaInicio
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(darkTheme = true, dynamicColor = false) {
                ContenidoPrincipalApp()
            }
        }
    }
}

/**
 * Aloja el Navigation Drawer centralizado de la aplicación y decide, según
 * la opción seleccionada, cuál de las pantallas existentes se muestra.
 * Es el único punto de la app que conoce la navegación entre pantallas.
 */
@Composable
private fun ContenidoPrincipalApp() {
    val estadoDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val alcanceCorutinas = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf(PantallaSeleccionada.RESUMEN) }

    fun abrirMenu() {
        alcanceCorutinas.launch { estadoDrawer.open() }
    }

    fun navegarA(pantalla: PantallaSeleccionada) {
        pantallaActual = pantalla
        alcanceCorutinas.launch { estadoDrawer.close() }
    }

    ModalNavigationDrawer(
        drawerState = estadoDrawer,
        scrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp),
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                PanelNavegacionLateral(
                    pantallaActual = pantallaActual,
                    alSeleccionarOpcion = { pantalla -> navegarA(pantalla) }
                )
            }
        }
    ) {
        when (pantallaActual) {
            PantallaSeleccionada.RESUMEN ->
                PantallaInicio(onMenuClick = { abrirMenu() })

            PantallaSeleccionada.CALENDARIO ->
                PantallaCalendario(onMenuClick = { abrirMenu() })

            PantallaSeleccionada.ASIGNATURAS ->
                PantallaAsignaturas(onMenuClick = { abrirMenu() })

            PantallaSeleccionada.CALIFICACIONES ->
                PantallaCalificaciones(onMenuClick = { abrirMenu() })

            PantallaSeleccionada.CUESTIONARIOS ->
                PantallaCuestionarios(onMenuClick = { abrirMenu() })
        }
    }
}