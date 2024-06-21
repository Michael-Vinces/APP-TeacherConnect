package com.example.teacherconnect.interfaces.canal

import android.util.Log
import com.example.teacherconnect.firebase.Canales
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.teacherconnect.firebase.Imagenes
import com.example.teacherconnect.firebase.Usuarios
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CanalViewModel : ViewModel(){
    private val firestore = FirebaseFirestore.getInstance()

    fun createCanal(canal: Canales, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val canalRef = FirebaseFirestore.getInstance().collection("canales").document()

        canalRef.set(canal.toMap()).addOnSuccessListener {
            canalRef.update("id", canalRef.id).addOnSuccessListener {
                onSuccess(canalRef.id)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun addCanalToUser(userId: String, canalId: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userRef.update("canales", FieldValue.arrayUnion(canalId))
            .addOnSuccessListener {
                Log.d("TeacherConnect", "Canal añadido al usuario")
            }
            .addOnFailureListener {
                Log.d("TeacherConnect", "Error añadiendo canal: ${it}")
            }
    }
    val canales = MutableLiveData<List<Canales>>()

    fun CanalesDelUsuario(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val canalIds = document["canales"] as? List<String> ?: listOf()
                if (canalIds.isNotEmpty()) {
                    db.collection("canales").whereIn("id", canalIds).get()
                        .addOnSuccessListener { querySnapshot ->
                            val canalesList = querySnapshot.documents.mapNotNull { it.toObject(Canales::class.java) }
                            canales.value = canalesList
                        }
                } else {
                    canales.value = listOf()
                }
            }

    }
    fun obtenerImagenesEmoji(): LiveData<List<Imagenes>> {
        val liveData = MutableLiveData<List<Imagenes>>()
        val db = FirebaseFirestore.getInstance()
        db.collection("imagenes")
            .whereEqualTo("categoria", "emoji")
            .get()
            .addOnSuccessListener { result ->
                val images = result.map { it.toObject(Imagenes::class.java) }
                liveData.value = images
            }
        return liveData
    }
    fun obtenerUsuarioPorId(userId: String?): LiveData<Usuarios?> {
        val liveData = MutableLiveData<Usuarios?>()
        val db = FirebaseFirestore.getInstance()

        // Lanzar una coroutine
        viewModelScope.launch {
            try {
                val snapshot = userId?.let { db.collection("users").document(it).get().await() }
                val user = snapshot?.toObject(Usuarios::class.java)
                liveData.value = user
            } catch (e: Exception) {
                val logMessage = e
                Log.d("MiTag", logMessage.toString())
            }
        }

        return liveData
    }


    fun unirUsuarioACanalPorPin(pin: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val canalRef = db.collection("canales")
            .whereEqualTo("pin", pin)

        canalRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val canalDocument = snapshot.documents[0]
                val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

                if (usuarioId != null) {
                    canalDocument.reference
                        .update("estudiantes", FieldValue.arrayUnion(usuarioId))
                        .addOnSuccessListener {
                            val usuarioRef = db.collection("users").document(usuarioId)
                            usuarioRef.update("canales", FieldValue.arrayUnion(canalDocument.id))
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { e -> onFailure(e) }
                        }
                        .addOnFailureListener { e -> onFailure(e) }
                } else {
                    onFailure(Exception("Usuario no autenticado"))
                }
            } else {
                onFailure(Exception("No se encontró canal con el PIN proporcionado."))
            }
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

}