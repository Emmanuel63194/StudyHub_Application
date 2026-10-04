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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.unicauca.aplimovil.studyhub_application.data.local.BaseDatosStudyHub
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.AsignaturaEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CalificacionEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity
import edu.unicauca.aplimovil.studyhub_application.ui.components.PanelNavegacionLateral
import edu.unicauca.aplimovil.studyhub_application.ui.components.PantallaSeleccionada
import edu.unicauca.aplimovil.studyhub_application.ui.screens.CrearAsignatura
import edu.unicauca.aplimovil.studyhub_application.ui.screens.CrearCalificacion
import edu.unicauca.aplimovil.studyhub_application.ui.screens.CrearCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.screens.DetalleAsignatura
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaAsignaturas
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCalendario
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCalificaciones
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaCuestionarios
import edu.unicauca.aplimovil.studyhub_application.ui.screens.PantallaInicio
import edu.unicauca.aplimovil.studyhub_application.ui.screens.RealizarCuestionario
import edu.unicauca.aplimovil.studyhub_application.ui.theme.AppTheme
import edu.unicauca.aplimovil.studyhub_application.ui.viewmodel.CalendarioViewModel
import edu.unicauca.aplimovil.studyhub_application.ui.viewmodel.CuestionarioViewModel
import edu.unicauca.aplimovil.studyhub_application.viewmodel.AsignaturaViewModel
import edu.unicauca.aplimovil.studyhub_application.viewmodel.CalificacionViewModel
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

    val calendarioViewModel: CalendarioViewModel = viewModel()

    val eventos by calendarioViewModel.eventos.collectAsState()

    val eventosCompletados by calendarioViewModel.eventosCompletados.collectAsState()


    val baseDatos = BaseDatosStudyHub.obtenerInstancia(LocalContext.current)

    val asignaturaViewModel: AsignaturaViewModel = viewModel(
        factory = fabricaViewModel {
            AsignaturaViewModel(
                baseDatos.asignaturaDao(),
                baseDatos.calificacionDao()
            )
        }
    )

    val calificacionViewModel: CalificacionViewModel = viewModel(
        factory = fabricaViewModel {
            CalificacionViewModel(
                baseDatos.calificacionDao()
            )
        }
    )

    val asignaturas by asignaturaViewModel.asignaturas.collectAsState()

    var asignaturaAEditar by remember {
        mutableStateOf<AsignaturaEntity?>(null)
    }


    var asignaturaDetalleId by remember {
        mutableStateOf<Int?>(null)
    }


    var calificacionAEditar by remember {
        mutableStateOf<CalificacionEntity?>(null)
    }


    var cuestionarioAEditar by remember {
        mutableStateOf<CuestionarioEntity?>(null)
    }


    var cuestionarioSeleccionadoId by remember {
        mutableStateOf<Long?>(null)
    }

    val entradaActual by navController.currentBackStackEntryAsState()

    val rutaActual = entradaActual?.destination?.route ?: "resumen"

    val pantallaActual = when (rutaActual) {
        "resumen" -> PantallaSeleccionada.RESUMEN
        "calendario" -> PantallaSeleccionada.CALENDARIO

        "asignaturas",
        "crearAsignatura",
        "detalleAsignatura" ->
            PantallaSeleccionada.ASIGNATURAS

        "calificaciones",
        "crearCalificacion" ->
            PantallaSeleccionada.CALIFICACIONES

        "cuestionarios",
        "crearCuestionario",
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

        gesturesEnabled =
            rutaActual != "crearCuestionario" &&
                    rutaActual != "realizarCuestionario" &&
                    rutaActual != "crearAsignatura" &&
                    rutaActual != "detalleAsignatura" &&
                    rutaActual != "crearCalificacion",

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
                    },

                    eventos = eventos,

                    eventosCompletados = eventosCompletados,

                    onAlternarCompletado = { eventoId ->
                        calendarioViewModel.alternarCompletado(eventoId)
                    },


                    onEliminarEvento = { evento ->
                        calendarioViewModel.eliminarEvento(evento)
                    }
                )
            }

            composable("calendario") {

                PantallaCalendario(
                    onMenuClick = {
                        abrirMenu()
                    },

                    eventos = eventos,

                    onGuardarEvento = { evento ->
                        calendarioViewModel.guardarEvento(evento)
                    },

                    onEliminarEvento = { evento ->
                        calendarioViewModel.eliminarEvento(evento)
                    }
                )
            }

            composable("asignaturas") {

                PantallaAsignaturas(
                    viewModel = asignaturaViewModel,

                    onMenuClick = {
                        abrirMenu()
                    },


                    onAgregarClick = {
                        asignaturaAEditar = null
                        navController.navigate("crearAsignatura")
                    },


                    onAsignaturaClick = { asignatura ->
                        asignaturaDetalleId = asignatura.id
                        navController.navigate("detalleAsignatura")
                    },


                    onEditarClick = { asignatura ->
                        asignaturaAEditar = asignatura
                        navController.navigate("crearAsignatura")
                    }
                )
            }

            composable(
                route = "crearAsignatura",


                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },


                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },


                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },


                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            ancho
                        }
                    )
                }

            ) {

                CrearAsignatura(
                    viewModel = asignaturaViewModel,
                    asignaturaAEditar = asignaturaAEditar,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "detalleAsignatura",

                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },

                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            ancho
                        }
                    )
                }

            ) {

                DetalleAsignatura(
                    asignatura = asignaturas.find {
                        it.id == asignaturaDetalleId
                    },
                    calificacionViewModel = calificacionViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("calificaciones") {

                PantallaCalificaciones(
                    calificacionViewModel = calificacionViewModel,
                    asignaturaViewModel = asignaturaViewModel,

                    onMenuClick = {
                        abrirMenu()
                    },

                    onAgregarClick = {
                        calificacionAEditar = null
                        navController.navigate("crearCalificacion")
                    },

                    onEditarClick = { calificacion ->
                        calificacionAEditar = calificacion
                        navController.navigate("crearCalificacion")
                    }
                )
            }

            composable(
                route = "crearCalificacion",

                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },

                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            ancho
                        }
                    )
                }

            ) {

                CrearCalificacion(
                    calificacionViewModel = calificacionViewModel,
                    asignaturaViewModel = asignaturaViewModel,
                    calificacionAEditar = calificacionAEditar,

                    onBack = {
                        navController.popBackStack()
                    },

                    onCrearAsignatura = {
                        asignaturaAEditar = null
                        navController.navigate("crearAsignatura")
                    }
                )
            }

            composable("cuestionarios") {

                PantallaCuestionarios(
                    cuestionarios = cuestionarios,

                    onMenuClick = {
                        abrirMenu()
                    },

                    onAgregarClick = {
                        cuestionarioAEditar = null

                        navController.navigate(
                            "crearCuestionario"
                        )
                    },

                    onEditarClick = { cuestionario ->

                        cuestionarioAEditar = cuestionario

                        navController.navigate(
                            "crearCuestionario"
                        )
                    },

                    onEliminarClick = { cuestionario ->

                        cuestionarioViewModel.eliminarCuestionario(
                            cuestionario
                        )
                    },

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

                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },

                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

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
                    },

                    cuestionarioAEditar = cuestionarioAEditar
                )
            }

            composable(
                route = "realizarCuestionario",

                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            ancho
                        }
                    )
                },

                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { ancho ->
                            -ancho
                        }
                    )
                },

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

private fun <VM : ViewModel> fabricaViewModel(
    crear: () -> VM
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {


        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T = crear() as T
    }

