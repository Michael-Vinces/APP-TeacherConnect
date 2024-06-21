package com.example.teacherconnect.firebase

import com.google.firebase.Timestamp

data class Mensajes(
    val id: String? = null,
    val canalID: String = "",
    val usuarioID: String = "",
    val contenido: String = "",
    val fecha: Timestamp? = null,
    val tipo: String = "",
    val imagenID: String? = null
) {
    fun toMap(): MutableMap<String, Any?> {
        val result: MutableMap<String, Any?> = mutableMapOf(
            "canalID" to this.canalID as Any?,
            "usuarioID" to this.usuarioID as Any?,
            "contenido" to this.contenido as Any?,
            "fecha" to this.fecha as Timestamp?,
            "tipo" to this.tipo as Any?
        )

        this.id?.let {
            result["id"] = it
        }

        this.imagenID?.let {
            result["imagenID"] = it as Any?
        }

        return result
    }
}
