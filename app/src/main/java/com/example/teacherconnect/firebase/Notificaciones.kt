package com.example.teacherconnect.firebase

import com.google.firebase.Timestamp

data class Notificaciones(
    var id: String?=null,
    val fecha: Timestamp? = null,
    val mensajeContent: String="",
    val canalId:String="",
    val usuarioId: String="",
){
    fun toMap(): MutableMap<String, Any?>{
        val result: MutableMap<String, Any?> = mutableMapOf(
            "fecha" to this.fecha,
            "mensajeContent" to this.mensajeContent,
            "canalId" to this.canalId,
            "usuarioId" to this.usuarioId
        )
        this.id?.let {
            result["id"] = it
        }

        return result
    }
}
