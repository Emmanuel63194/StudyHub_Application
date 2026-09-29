package edu.unicauca.aplimovil.studyhub_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.unicauca.aplimovil.studyhub_application.ui.components.PanelNavegacionLateral
import edu.unicauca.aplimovil.studyhub_application.ui.components.PantallaSeleccionada
import edu.unicauca.aplimovil.studyhub_application.ui.screens.CrearCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.screens.EditarCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaAsignaturas
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCalendario
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCalificaciones
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCuestionarios
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaInicio
import edu.unicauca.aplimovil.studyhub_application.ui.screens.RealizarCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.viewmodel.CuestionarioViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme(
                darkTheme = true,
                dynamicColor = false
            ) {
                ContenidoPrincipalApp()
            }
        }
    }
}

@Composable
private fun ContenidoPrincipalApp() {

    val navController = rememberNavController()

    val estadoDrawer = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val alcanceCorutinas = rememberCoroutineScope()

    val cuestionarioViewModel: CuestionarioViewModel = viewModel()

    val cuestionarios by cuestionarioViewModel.cuestionarios.collectAsState()

    // Cuestionario seleccionado para realizar.
    var cuestionarioSeleccionadoId by remember {
        mutableStateOf<Long?>(null)
    }

    // Cuestionario seleccionado para editar.
    var cuestionarioSeleccionadoParaEditarId by remember {
        mutableStateOf<Long?>(null)
    }

    val entradaActual by navController.currentBackStackEntryAsState()

    val rutaActual = entradaActual?.destination?.route ?: "resumen"

    val pantallaActual = when (rutaActual) {
        "resumen" -> PantallaSeleccionada.RESUMEN
        "calendario" -> PantallaSeleccionada.CALENDARIO
        "asignaturas" -> PantallaSeleccionada.ASIGNATURAS
        "calificaciones" -> PantallaSeleccionada.CALIFICACIONES

        "cuestionarios",
        "crearCuestionario",
        "editarCuestionario",
        "realizarCuestionario" ->
            PantallaSeleccionada.CUESTIONARIOS

        else -> PantallaSeleccionada.RESUMEN
    }

    fun abrirMenu() {
        alcanceCorutinas.launch {
            estadoDrawer.open()
        }
    }

    fun navegarA(pantalla: PantallaSeleccionada) {

        val ruta = when (pantalla) {
            PantallaSeleccionada.RESUMEN -> "resumen"
            PantallaSeleccionada.CALENDARIO -> "calendario"
            PantallaSeleccionada.ASIGNATURAS -> "asignaturas"
            PantallaSeleccionada.CALIFICACIONES -> "calificaciones"
            PantallaSeleccionada.CUESTIONARIOS -> "cuestionarios"
        }

        navController.navigate(ruta)

        alcanceCorutinas.launch {
            estadoDrawer.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = estadoDrawer,

        // El panel lateral solo se puede abrir mediante gesto
        // en las pantallas principales.
        gesturesEnabled =
            rutaActual != "crearCuestionario" &&
                    rutaActual != "editarCuestionario" &&
                    rutaActual != "realizarCuestionario",

        scrimColor = androidx.compose.ui.graphics.Color.Black.copy(
            alpha = 0.5f
        ),

        drawerContent = {

            ModalDrawerSheet(
                modifier = Modifier.width(310.dp),
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {

                PanelNavegacionLateral(
                    pantallaActual = pantallaActual,
                    alSeleccionarOpcion = { pantalla ->
                        navegarA(pantalla)
                    }
                )
            }
        }
    ) {

        NavHost(
            navController = navController,
            startDestination = "resumen",

            // Las pantallas principales no tienen transición.
            enterTransition = {
                EnterTransition.None
            },

            exitTransition = {
                ExitTransition.None
            },

            popEnterTransition = {
                EnterTransition.None
            },

            popExitTransition = {
                ExitTransition.None
            }

        ) {

            composable("resumen") {

                PantallaInicio(
                    onMenuClick = {
                        abrirMenu()
                    }
                )
            }

            composable("calendario") {

                PantallaCalendario(
                    onMenuClick = {
                        abrirMenu()
                    }
                )
            }

            composable("asignaturas") {

                PantallaAsignaturas(
                    onMenuClick = {
                        abrirMenu()
                    }
                )
            }

            composable("calificaciones") {

                PantallaCalificaciones(
                    onMenuClick = {
                        abrirMenu()
                    }
                )
            }

            composable("cuestionarios") {

                PantallaCuestionarios(
                    cuestionarios = cuestionarios,

                    onMenuClick = {
                        abrirMenu()
                    },

                    // Abrir CrearCuestionario.
                    onAgregarClick = {
                        navController.navigate(
                            "crearCuestionario"
                        )
                    },

                    // Abrir EditarCuestionario.
                    onEditarClick = { cuestionario ->

                        cuestionarioSeleccionadoParaEditarId =
                            cuestionario.id

                        navController.navigate(
                            "editarCuestionario"
                        )
                    },

                    // Eliminar cuestionario.
                    onEliminarClick = { cuestionario ->

                        cuestionarioViewModel.eliminarCuestionario(
                            cuestionario
                        )
                    },

                    // Abrir RealizarCuestionario.
                    onCuestionarioClick = { cuestionario ->

                        cuestionarioSeleccionadoId =
                            cuestionario.id

                        navController.navigate(
                            "realizarCuestionario"
                        )
                    }
                )
            }

            composable(
                route = "crearCuestionario",

                // La pantalla entra desde la derecha.
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },

                // La pantalla actual sale hacia la izquierda.
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                // Al volver, CrearCuestionario entra desde la izquierda.
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                // Al volver, la pantalla sale hacia la derecha.
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            ancho
                        }
                    )
                }

            ) {

                CrearCuestionario(
                    onVolver = {
                        navController.popBackStack()
                    },

                    onCreado = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "editarCuestionario",

                // La pantalla entra desde la derecha.
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },

                // La pantalla actual sale hacia la izquierda.
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                // Al volver, EditarCuestionario entra desde la izquierda.
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                // Al volver, la pantalla sale hacia la derecha.
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            ancho
                        }
                    )
                }

            ) {

                val cuestionario = cuestionarios.find {
                    it.id == cuestionarioSeleccionadoParaEditarId
                }

                if (cuestionario != null) {

                    EditarCuestionario(
                        cuestionario = cuestionario,

                        onVolver = {
                            navController.popBackStack()
                        },

                        onEditado = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable(
                route = "realizarCuestionario",

                // La pantalla entra desde la derecha.
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },

                // La pantalla actual sale hacia la izquierda.
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                // Al volver, RealizarCuestionario entra desde la izquierda.
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                // Al volver, la pantalla sale hacia la derecha.
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            ancho
                        }
                    )
                }

            ) {

                val cuestionario = cuestionarios.find {
                    it.id == cuestionarioSeleccionadoId
                }

                RealizarCuestionario(
                    cuestionario = cuestionario,

                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
