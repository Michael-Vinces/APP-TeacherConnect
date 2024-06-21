package com.example.teacherconnect.firebase

data class Actividades(
    val id: String?=null,
    val horarioId: String="",
    val dia:String="",
    val horaEntrada:String="",
    val horaSalida:String="",
    val nombre:String="",
    val aula:String=""
){
    fun toMap(): MutableMap<String, String> {
        val result = mutableMapOf(
            "horarioId" to this.horarioId,
            "dia" to this.dia,
            "horaEntrada" to this.horaEntrada,
            "horaSalida" to this.horaSalida,
            "nombre" to this.nombre,
            "aula" to this.aula
        )
        this.id?.let {
            result["id"] = it
        }

        return result
    }
}
