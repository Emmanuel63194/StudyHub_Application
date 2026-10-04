package edu.unicauca.aplimovil.studyhub_application.data.local

import androidx.room.TypeConverter
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.PreguntaCuestionario
import edu.unicauca.aplimovil.studyhub_application.data.local.entity.RespuestaCuestionario
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun desdePreguntas(preguntas: List<PreguntaCuestionario>): String {
        val arregloPreguntas = JSONArray()
        preguntas.forEach { pregunta ->
            val arregloRespuestas = JSONArray()
            pregunta.respuestas.forEach { respuesta ->
                arregloRespuestas.put(
                    JSONObject()
                        .put("texto", respuesta.texto)
                        .put("esCorrecta", respuesta.esCorrecta)
                )
            }
            arregloPreguntas.put(
                JSONObject()
                    .put("texto", pregunta.texto)
                    .put("respuestas", arregloRespuestas)
            )
        }
        return arregloPreguntas.toString()
    }

    @TypeConverter
    fun aPreguntas(json: String): List<PreguntaCuestionario> {
        if (json.isBlank()) return emptyList()
        val arregloPreguntas = JSONArray(json)
        return List(arregloPreguntas.length()) { i ->
            val objetoPregunta = arregloPreguntas.getJSONObject(i)
            val arregloRespuestas = objetoPregunta.getJSONArray("respuestas")
            PreguntaCuestionario(
                texto = objetoPregunta.getString("texto"),
                respuestas = List(arregloRespuestas.length()) { j ->
                    val objetoRespuesta = arregloRespuestas.getJSONObject(j)
                    RespuestaCuestionario(
                        texto = objetoRespuesta.getString("texto"),
                        esCorrecta = objetoRespuesta.getBoolean("esCorrecta")
                    )
                }
            )
        }
    }
}