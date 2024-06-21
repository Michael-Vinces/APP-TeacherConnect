package com.example.teacherconnect.firebase

data class Imagenes(
    val id: String?=null,
    val categoria:String="",
    val nombre:String="",
    val url:String=""
){
    fun toMap(): Map<String, Any> {
        val result = mutableMapOf(
            "categoria" to this.categoria,
            "nombre" to this.nombre,
            "url" to this.url
        )
        this.id?.let {
            result["id"] = it
        }
        return result
    }
}