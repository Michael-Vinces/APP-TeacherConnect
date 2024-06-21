package com.example.teacherconnect.firebase

data class Horarios(
    val id: String?=null,
    val titulo: String="",
    val usuarioid: String="",
    val actividades: List<String> = listOf()

){
    fun toMap(): MutableMap<String, Any>{
        val result = mutableMapOf(
            "titulo" to this.titulo,
            "usuarioid" to this.usuarioid,
            "actividades" to this.actividades
        )
        this.id?.let {
            result["id"] = it
        }

        return result
    }

}
