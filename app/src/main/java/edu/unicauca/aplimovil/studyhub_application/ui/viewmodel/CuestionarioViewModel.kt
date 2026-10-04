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
        BaseDatosStudyHub.obtenerInstancia(application).cuestionarioDao()

    val cuestionarios: StateFlow<List<CuestionarioEntity>> =
        cuestionarioDao.obtenerTodos()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun eliminarCuestionario(cuestionario: CuestionarioEntity) {
        viewModelScope.launch {
            cuestionarioDao.eliminar(cuestionario)
        }
    }

    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo.asStateFlow()

    private val _preguntas =
        MutableStateFlow(listOf(crearPreguntaVacia()))

    val preguntas: StateFlow<List<PreguntaCuestionario>> =
        _preguntas.asStateFlow()

    private val _intentoCrear = MutableStateFlow(false)

    val intentoCrear: StateFlow<Boolean> =
        _intentoCrear.asStateFlow()

    fun actualizarTitulo(nuevoTitulo: String) {
        _titulo.value = nuevoTitulo
    }

    fun crearPreguntaVacia(): PreguntaCuestionario =
        PreguntaCuestionario(
            texto = "",
            respuestas = List(3) {
                RespuestaCuestionario(
                    texto = "",
                    esCorrecta = false
                )
            }
        )

    fun cargarCuestionarioParaEditar(
        cuestionario: CuestionarioEntity
    ) {
        _titulo.value = cuestionario.titulo
        _preguntas.value = cuestionario.preguntas
        _intentoCrear.value = false
    }

    fun agregarPregunta() {
        _preguntas.value =
            _preguntas.value + crearPreguntaVacia()
    }

    fun eliminarPregunta(indice: Int) {
        if (indice < 1 || indice >= _preguntas.value.size) return

        _preguntas.value =
            _preguntas.value.filterIndexed { i, _ ->
                i != indice
            }
    }

    fun actualizarTextoPregunta(
        indicePregunta: Int,
        texto: String
    ) {
        modificarPregunta(indicePregunta) {
            it.copy(texto = texto)
        }
    }

    fun actualizarTextoRespuesta(
        indicePregunta: Int,
        indiceRespuesta: Int,
        texto: String
    ) {
        modificarPregunta(indicePregunta) { pregunta ->
            pregunta.copy(
                respuestas = pregunta.respuestas.mapIndexed { i, r ->
                    if (i == indiceRespuesta) {
                        r.copy(texto = texto)
                    } else {
                        r
                    }
                }
            )
        }
    }

    fun marcarRespuestaCorrecta(
        indicePregunta: Int,
        indiceRespuesta: Int
    ) {
        modificarPregunta(indicePregunta) { pregunta ->
            pregunta.copy(
                respuestas = pregunta.respuestas.mapIndexed { i, r ->
                    r.copy(
                        esCorrecta = i == indiceRespuesta
                    )
                }
            )
        }
    }

    fun eliminarRespuesta(
        indicePregunta: Int,
        indiceRespuesta: Int
    ) {
        modificarPregunta(indicePregunta) { pregunta ->
            if (
                pregunta.respuestas.size == 3 &&
                indiceRespuesta == 2
            ) {
                pregunta.copy(
                    respuestas = pregunta.respuestas.take(2)
                )
            } else {
                pregunta
            }
        }
    }

    private fun modificarPregunta(
        indice: Int,
        transformacion: (PreguntaCuestionario) -> PreguntaCuestionario
    ) {
        _preguntas.value =
            _preguntas.value.mapIndexed { i, p ->
                if (i == indice) {
                    transformacion(p)
                } else {
                    p
                }
            }
    }

    fun tituloEsValido(): Boolean =
        _titulo.value.isNotBlank()

    fun mensajeErrorPregunta(
        pregunta: PreguntaCuestionario
    ): String? {

        val falta =
            pregunta.texto.isBlank()

        val faltanRespuestas =
            pregunta.respuestas.count {
                it.texto.isNotBlank()
            } < 2

        val faltaCorrecta =
            pregunta.respuestas.none {
                it.esCorrecta &&
                        it.texto.isNotBlank()
            }

        return when {
            falta &&
                    faltanRespuestas &&
                    faltaCorrecta ->
                "(Falta la pregunta, 2 respuestas, opcion correcta)"

            falta &&
                    !faltanRespuestas &&
                    !faltaCorrecta ->
                "(Falta la pregunta)"

            !falta &&
                    faltanRespuestas &&
                    !faltaCorrecta ->
                "(Falta 2 respuestas)"

            !falta &&
                    !faltanRespuestas &&
                    faltaCorrecta ->
                "(falta la opcion correcta)"

            falta &&
                    faltanRespuestas ->
                "(Falta la pregunta, 2 respuestas)"

            falta &&
                    faltaCorrecta ->
                "(Falta la pregunta, opcion correcta)"

            faltanRespuestas &&
                    faltaCorrecta ->
                "(Falta 2 respuestas, opcion correcta)"

            else ->
                null
        }
    }

    fun preguntaEsValida(
        pregunta: PreguntaCuestionario
    ): Boolean =
        mensajeErrorPregunta(pregunta) == null

    fun intentarCrear(
        onGuardado: (Long) -> Unit = {}
    ) {
        _intentoCrear.value = true
        guardarCuestionario(onGuardado)
    }

    fun guardarCuestionario(
        onGuardado: (Long) -> Unit = {}
    ) {
        if (!tituloEsValido()) return

        if (!_preguntas.value.all {
                preguntaEsValida(it)
            }
        ) {
            return
        }

        val preguntasLimpias =
            _preguntas.value.map { pregunta ->

                PreguntaCuestionario(
                    texto = pregunta.texto.trim(),
                    respuestas = pregunta.respuestas
                        .filter {
                            it.texto.isNotBlank()
                        }
                        .map {
                            it.copy(
                                texto = it.texto.trim()
                            )
                        }
                )
            }

        viewModelScope.launch {

            val id = cuestionarioDao.insertar(
                CuestionarioEntity(
                    titulo = _titulo.value.trim(),
                    preguntas = preguntasLimpias
                )
            )

            onGuardado(id)
        }
    }

    fun guardarCuestionarioGenerado(
        titulo: String,
        preguntas: List<PreguntaCuestionario>,
        onGuardado: (Long) -> Unit = {}
    ) {
        val tituloLimpio = titulo.trim()

        if (tituloLimpio.isBlank()) return
        if (preguntas.isEmpty()) return

        if (!preguntas.all {
                preguntaEsValida(it)
            }
        ) {
            return
        }

        val preguntasLimpias =
            preguntas.map { pregunta ->

                PreguntaCuestionario(
                    texto = pregunta.texto.trim(),
                    respuestas = pregunta.respuestas
                        .filter {
                            it.texto.isNotBlank()
                        }
                        .map {
                            it.copy(
                                texto = it.texto.trim()
                            )
                        }
                )
            }

        viewModelScope.launch {

            val id = cuestionarioDao.insertar(
                CuestionarioEntity(
                    titulo = tituloLimpio,
                    preguntas = preguntasLimpias
                )
            )

            onGuardado(id)
        }
    }

    fun editarCuestionario(
        cuestionario: CuestionarioEntity,
        onGuardado: () -> Unit = {}
    ) {
        _intentoCrear.value = true

        if (!tituloEsValido()) return

        if (!_preguntas.value.all {
                preguntaEsValida(it)
            }
        ) {
            return
        }

        val preguntasLimpias =
            _preguntas.value.map { pregunta ->

                PreguntaCuestionario(
                    texto = pregunta.texto.trim(),
                    respuestas = pregunta.respuestas
                        .filter {
                            it.texto.isNotBlank()
                        }
                        .map {
                            it.copy(
                                texto = it.texto.trim()
                            )
                        }
                )
            }

        viewModelScope.launch {

            val cuestionarioActualizado =
                cuestionario.copy(
                    titulo = _titulo.value.trim(),
                    preguntas = preguntasLimpias
                )

            cuestionarioDao.actualizar(
                cuestionarioActualizado
            )

            onGuardado()
        }
    }
}