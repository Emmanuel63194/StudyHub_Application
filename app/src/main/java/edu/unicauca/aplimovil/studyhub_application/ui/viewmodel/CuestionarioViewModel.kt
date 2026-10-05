package edu.unicauca.aplimovil.studyhub_application.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.unicauca.aplimovil.studyhub_application.data.local.BaseDatosStudyHub
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.CuestionarioEntity
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.PreguntaCuestionario
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.RespuestaCuestionario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CuestionarioViewModel(application: Application) : AndroidViewModel(application) {

    private val cuestionarioDao =
        BaseDatosStudyHub.obtenerInstancia(application).cuestionarioDao() //Obtiene el dao de cuestionarios para realizar operaciones con los datos

    val cuestionarios: StateFlow<List<CuestionarioEntity>> =
        cuestionarioDao.obtenerTodos() //Obtiene la lista de cuestionarios guardados
            .stateIn( //Mantiene la lista actualizada
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun eliminarCuestionario(cuestionario: CuestionarioEntity) { //Una funcion que se utiliza para eliminar un cuestionario
        viewModelScope.launch {
            cuestionarioDao.eliminar(cuestionario) //Le indica al dao que elimine el cuestionario seleccionado
        }
    }

    private val _titulo = MutableStateFlow("") //Guarda temporalmente el titulo que se va escribiendo
    val titulo: StateFlow<String> = _titulo.asStateFlow() //Permite consultar en pantalla cual es el titulo actual

    private val _preguntas = //Guarda todas las preguntas que se estan creando
        MutableStateFlow(listOf(crearPreguntaVacia()))

    val preguntas: StateFlow<List<PreguntaCuestionario>> = //Permite que la pantalla pueda consultar cuales son las preguntas actuales
        _preguntas.asStateFlow()

    private val _intentoCrear = MutableStateFlow(false) //Guarda si el usuario ya intento crear el cuestionario

    val intentoCrear: StateFlow<Boolean> = //Permite a la interfaz saber si el usuario ya intento crear el cuestionario
        _intentoCrear.asStateFlow()

    fun actualizarTitulo(nuevoTitulo: String) { //Cada vez que el usuario escribe o modifica el titulo se reemplaza el titulo anterior por el nuevo
        _titulo.value = nuevoTitulo //Actualiza el titulo que se esta guardando temporalmente
    }

    fun crearPreguntaVacia(): PreguntaCuestionario = //Crea una pregunta vacia sin contenido
        PreguntaCuestionario(
            texto = "", //La pregunta comienza sin ningun texto
            respuestas = List(3) { //Se crean inicialmente 3 respuestas
                RespuestaCuestionario(
                    texto = "", //Cada respuesta comienza sin ningun texto
                    esCorrecta = false //Cada respuesta comienza marcada como incorrecta
                )
            }
        )

    fun cargarCuestionarioParaEditar(
        cuestionario: CuestionarioEntity //Recibe un cuestionario que ya existe y prepara los datos para que se pueda modificar
    ) {
        _titulo.value = cuestionario.titulo //Coloca el titulo existente en el campo de titulo
        _preguntas.value = cuestionario.preguntas //Carga las preguntas y respuestas que tenia guardadas el cuestionario
        _intentoCrear.value = false //Indica que el usuario aun no ha intentado guardar la edicion del cuestionario
    }

    fun agregarPregunta() { //Se utiliza cuando el usuario quiere agregar una pregunta
        _preguntas.value =
            _preguntas.value + crearPreguntaVacia() //Toma las preguntas existentes y agrega una pregunta vacia al final
    }

    fun eliminarPregunta(indice: Int) { //Se utiliza para eliminar una pregunta especifica
        if (indice < 1 || indice >= _preguntas.value.size) return //La primera pregunta nunca se puede eliminar

        _preguntas.value = //Crea nuevamente la lista dejando por fuera la pregunta que se quiere eliminar
            _preguntas.value.filterIndexed { i, _ ->
                i != indice //Conserva todas las preguntas excepto la que coincide con el indice indicado
            }
    }

    fun actualizarTextoPregunta( //Esta funcion se utiliza para cambiar el texto de una pregunta especifica
        indicePregunta: Int,
        texto: String
    ) {
        modificarPregunta(indicePregunta) { //Busca la pregunta que se quiere modificar
            it.copy(texto = texto) //Reemplaza el texto anterior por el nuevo
        }
    }

    fun actualizarTextoRespuesta(
        indicePregunta: Int, //Indica cual es la pregunta que contiene la respuesta
        indiceRespuesta: Int, //Indica cual es la respuesta que se quiere modificar
        texto: String //Contiene el nuevo texto de la respuesta
    ) {
        modificarPregunta(indicePregunta) { pregunta -> //Busca la pregunta que se quiere modificar
            pregunta.copy(
                respuestas = pregunta.respuestas.mapIndexed { i, r -> //Recorre todas las respuestas de esa pregunta
                    if (i == indiceRespuesta) { //Comprueba si esta es la respuesta que se quiere modificar
                        r.copy(texto = texto) //Reemplaza el texto anterior de esa respuesta
                    } else {
                        r //Mantiene las demas respuestas sin cambios
                    }
                }
            )
        }
    }

    fun marcarRespuestaCorrecta(
        indicePregunta: Int, //Indica en cual pregunta se encuentra la respuesta
        indiceRespuesta: Int //Indica cual respuesta se va a marcar como correcta
    ) {
        modificarPregunta(indicePregunta) { pregunta -> //Busca la pregunta que se quiere modificar
            pregunta.copy(
                respuestas = pregunta.respuestas.mapIndexed { i, r -> //Recorre todas las respuestas de esa pregunta
                    r.copy(
                        esCorrecta = i == indiceRespuesta //Marca como correcta solamente la respuesta seleccionada
                    )
                }
            )
        }
    }

    fun eliminarRespuesta(
        indicePregunta: Int, //Indica en cual pregunta se encuentra la respuesta
        indiceRespuesta: Int //Indica cual respuesta se quiere eliminar
    ) {
        modificarPregunta(indicePregunta) { pregunta -> //Busca la pregunta que contiene la respuesta
            if (
                pregunta.respuestas.size == 3 && //Comprueba que actualmente existan 3 respuestas
                indiceRespuesta == 2 //Comprueba que se este intentando eliminar la tercera respuesta
            ) {
                pregunta.copy(
                    respuestas = pregunta.respuestas.take(2) //Deja solamente las primeras 2 respuestas
                )
            } else {
                pregunta //Si no se cumple la condicion, mantiene la pregunta sin cambios
            }
        }
    }

    private fun modificarPregunta(
        indice: Int, //Indica cual pregunta se quiere modificar
        transformacion: (PreguntaCuestionario) -> PreguntaCuestionario //Indica el cambio que se debe realizar en esa pregunta
    ) {
        _preguntas.value =
            _preguntas.value.mapIndexed { i, p -> //Recorre todas las preguntas de la lista
                if (i == indice) { //Comprueba si esta es la pregunta que se quiere modificar
                    transformacion(p) //Aplica el cambio solamente a la pregunta seleccionada
                } else {
                    p //Mantiene las demas preguntas sin cambios
                }
            }
    }

    fun tituloEsValido(): Boolean =
        _titulo.value.isNotBlank() //Comprueba que el titulo tenga algun texto

    fun mensajeErrorPregunta(
        pregunta: PreguntaCuestionario //Recibe la pregunta que se quiere comprobar
    ): String? {

        val falta =
            pregunta.texto.isBlank() //Comprueba si el texto de la pregunta esta vacio

        val faltanRespuestas =
            pregunta.respuestas.count { //Cuenta cuantas respuestas tienen texto
                it.texto.isNotBlank() //Comprueba que cada respuesta tenga algun texto
            } < 2 //Indica que deben existir al menos 2 respuestas escritas

        val faltaCorrecta =
            pregunta.respuestas.none { //Comprueba si no existe ninguna respuesta correcta
                it.esCorrecta && //Comprueba que la respuesta este marcada como correcta
                        it.texto.isNotBlank() //Comprueba que la respuesta tenga texto
            }

        return when { //Elige el mensaje dependiendo de lo que le falte a la pregunta
            falta &&
                    faltanRespuestas &&
                    faltaCorrecta ->
                "(Falta la pregunta, 2 respuestas, opcion correcta)" //Indica que faltan las tres cosas

            falta &&
                    !faltanRespuestas &&
                    !faltaCorrecta ->
                "(Falta la pregunta)" //Indica que solamente falta escribir la pregunta

            !falta &&
                    faltanRespuestas &&
                    !faltaCorrecta ->
                "(Falta 2 respuestas)" //Indica que faltan al menos 2 respuestas escritas

            !falta &&
                    !faltanRespuestas &&
                    faltaCorrecta ->
                "(falta la opcion correcta)" //Indica que no se ha seleccionado una respuesta correcta

            falta &&
                    faltanRespuestas ->
                "(Falta la pregunta, 2 respuestas)" //Indica que faltan la pregunta y las respuestas necesarias

            falta &&
                    faltaCorrecta ->
                "(Falta la pregunta, opcion correcta)" //Indica que falta la pregunta y seleccionar una respuesta correcta

            faltanRespuestas &&
                    faltaCorrecta ->
                "(Falta 2 respuestas, opcion correcta)" //Indica que faltan respuestas y seleccionar una correcta

            else ->
                null //Indica que la pregunta esta completa y no tiene errores
        }
    }

    fun preguntaEsValida(
        pregunta: PreguntaCuestionario //Recibe una pregunta que se quiere comprobar
    ): Boolean =
        mensajeErrorPregunta(pregunta) == null //Devuelve verdadero cuando la pregunta no tiene ningun error

    fun intentarCrear(
        onGuardado: (Long) -> Unit = {} //Permite realizar una accion despues de guardar el cuestionario
    ) {
        _intentoCrear.value = true //Indica que el usuario intento crear el cuestionario
        guardarCuestionario(onGuardado) //Intenta guardar el cuestionario
    }

    fun guardarCuestionario(
        onGuardado: (Long) -> Unit = {} //Permite realizar una accion cuando el cuestionario se guarda correctamente
    ) {
        if (!tituloEsValido()) return //Si el titulo esta vacio, no continua con el guardado

        if (!_preguntas.value.all { //Comprueba todas las preguntas del cuestionario
                preguntaEsValida(it) //Comprueba que cada pregunta sea valida
            }
        ) {
            return //Si alguna pregunta tiene errores, no se guarda el cuestionario
        }

        val preguntasLimpias =
            _preguntas.value.map { pregunta -> //Recorre todas las preguntas para prepararlas antes de guardarlas

                PreguntaCuestionario(
                    texto = pregunta.texto.trim(), //Elimina espacios innecesarios al inicio y al final de la pregunta
                    respuestas = pregunta.respuestas
                        .filter {
                            it.texto.isNotBlank() //Elimina las respuestas que esten completamente vacias
                        }
                        .map {
                            it.copy(
                                texto = it.texto.trim() //Elimina espacios innecesarios al inicio y al final de cada respuesta
                            )
                        }
                )
            }

        viewModelScope.launch { //Inicia el proceso para guardar el cuestionario

            val id = cuestionarioDao.insertar( //Pide al dao que guarde el cuestionario y obtiene su identificador
                CuestionarioEntity(
                    titulo = _titulo.value.trim(), //Guarda el titulo sin espacios innecesarios
                    preguntas = preguntasLimpias //Guarda las preguntas que ya fueron revisadas y limpiadas
                )
            )

            onGuardado(id) //Indica que el cuestionario fue guardado y devuelve su identificador
        }
    }

    fun guardarCuestionarioGenerado(
        titulo: String, //Recibe el titulo del cuestionario generado
        preguntas: List<PreguntaCuestionario>, //Recibe las preguntas generadas
        onGuardado: (Long) -> Unit = {} //Permite realizar una accion despues de guardar el cuestionario generado
    ) {
        val tituloLimpio = titulo.trim() //Elimina espacios innecesarios del titulo generado

        if (tituloLimpio.isBlank()) return //Si el titulo generado esta vacio, no continua
        if (preguntas.isEmpty()) return //Si no se generaron preguntas, no continua

        if (!preguntas.all { //Comprueba todas las preguntas generadas
                preguntaEsValida(it) //Comprueba que cada pregunta generada sea valida
            }
        ) {
            return //Si alguna pregunta generada tiene errores, no se guarda
        }

        val preguntasLimpias =
            preguntas.map { pregunta -> //Recorre las preguntas generadas para prepararlas antes de guardarlas

                PreguntaCuestionario(
                    texto = pregunta.texto.trim(), //Elimina espacios innecesarios al inicio y al final de la pregunta
                    respuestas = pregunta.respuestas
                        .filter {
                            it.texto.isNotBlank() //Elimina las respuestas generadas que esten vacias
                        }
                        .map {
                            it.copy(
                                texto = it.texto.trim() //Elimina espacios innecesarios al inicio y al final de cada respuesta generada
                            )
                        }
                )
            }

        viewModelScope.launch { //Inicia el proceso para guardar el cuestionario generado

            val id = cuestionarioDao.insertar( //Pide al dao que guarde el cuestionario generado y obtiene su identificador
                CuestionarioEntity(
                    titulo = tituloLimpio, //Guarda el titulo generado sin espacios innecesarios
                    preguntas = preguntasLimpias //Guarda las preguntas generadas ya revisadas y limpiadas
                )
            )

            onGuardado(id) //Indica que el cuestionario generado fue guardado correctamente
        }
    }

    fun editarCuestionario(
        cuestionario: CuestionarioEntity, //Recibe el cuestionario que se quiere modificar
        onGuardado: () -> Unit = {} //Permite realizar una accion cuando la edicion termine correctamente
    ) {
        _intentoCrear.value = true //Indica que el usuario intento guardar los cambios

        if (!tituloEsValido()) return //Si el titulo esta vacio, no continua

        if (!_preguntas.value.all { //Comprueba todas las preguntas antes de actualizar
                preguntaEsValida(it) //Comprueba que cada pregunta sea valida
            }
        ) {
            return //Si alguna pregunta tiene errores, no actualiza el cuestionario
        }

        val preguntasLimpias =
            _preguntas.value.map { pregunta -> //Prepara las preguntas antes de actualizar el cuestionario

                PreguntaCuestionario(
                    texto = pregunta.texto.trim(), //Elimina espacios innecesarios de la pregunta
                    respuestas = pregunta.respuestas
                        .filter {
                            it.texto.isNotBlank() //Elimina las respuestas que esten vacias
                        }
                        .map {
                            it.copy(
                                texto = it.texto.trim() //Elimina espacios innecesarios de cada respuesta
                            )
                        }
                )
            }

        viewModelScope.launch { //Inicia el proceso para actualizar el cuestionario

            val cuestionarioActualizado =
                cuestionario.copy( //Crea una nueva version del cuestionario con los cambios realizados
                    titulo = _titulo.value.trim(), //Actualiza el titulo del cuestionario
                    preguntas = preguntasLimpias //Actualiza las preguntas del cuestionario
                )

            cuestionarioDao.actualizar( //Pide al dao que actualice el cuestionario en la base de datos
                cuestionarioActualizado
            )

            onGuardado() //Indica que la edicion se guardo correctamente
        }
    }
}

