package com.example.teacherconnect.interfaces.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teacherconnect.firebase.Canales
import com.example.teacherconnect.firebase.Imagenes
import com.example.teacherconnect.firebase.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _usuario = MutableLiveData<Usuarios?>(null)
    val usuario: LiveData<Usuarios?> get() = _usuario
    private val _urlFotoPerfil = MutableLiveData<String?>(null)
    val urlFotoPerfil: LiveData<String?> get() = _urlFotoPerfil

    init {
        fetchUserData()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            contarCanalesDelUsuario(userId)
        }
    }
    fun NotificacionesCheck(userId: String, onCheckComplete: (Boolean) -> Unit) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userRef.get().addOnSuccessListener { documentSnapshot ->
            val notificaciones = documentSnapshot.get("notificaciones") as? List<*>
            onCheckComplete(notificaciones != null && notificaciones.isNotEmpty())
        }.addOnFailureListener { exception ->
            Log.d("AppLog", "Error al verificar notificaciones: ${exception.message}")
        }
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(Usuarios::class.java)
            if (user != null) {
                _usuario.postValue(user)
                fetchFotoPerfil(user.fotoPerfilId ?: "")
            } else {
                Log.d("fetchUserData", "Usuario no encontrado en Firestore")
            }
        }.addOnFailureListener { e ->
            Log.e("fetchUserData", "Error al obtener datos del usuario: ${e.message}")
        }
    }

    private fun fetchFotoPerfil(fotoPerfilId: String) {
        if (fotoPerfilId.isNotEmpty()) {
            db.collection("imagenes").document(fotoPerfilId).get().addOnSuccessListener { documentSnapshot ->
                val url = documentSnapshot.getString("url")
                _urlFotoPerfil.postValue(url)
            }.addOnFailureListener { e ->
                Log.e("fetchFotoPerfil", "Error al obtener la foto de perfil: ${e.message}")
            }
        }
    }
    private val _numeroDeCanales = mutableStateOf(0)
    val numeroDeCanales: State<Int> get() = _numeroDeCanales

    fun contarCanalesDelUsuario(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val canalIds = document["canales"] as? List<String> ?: listOf()
                _numeroDeCanales.value = canalIds.size
            }.addOnFailureListener { e ->
                Log.e("contarCanalesDelUsuario", "Error al contar canales del usuario: ${e.message}")
            }
    }

    fun obtenerImagenesFotoPerfil(): LiveData<List<Imagenes>> {
        val liveData = MutableLiveData<List<Imagenes>>()
        val db = FirebaseFirestore.getInstance()
        db.collection("imagenes")
            .whereEqualTo("categoria", "fotoPerfil")
            .get()
            .addOnSuccessListener { result ->
                val images = result.map { it.toObject(Imagenes::class.java) }
                liveData.value = images
            }.addOnFailureListener { e ->
                Log.e("obtenerImagenesFotoPerfil", "Error al obtener imágenes de foto de perfil: ${e.message}")
            }
        return liveData
    }

    fun actualizarFotoPerfil(nuevaFotoPerfilId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).update("fotoPerfilId", nuevaFotoPerfilId)
            .addOnSuccessListener {
                Log.d("actualizarFotoPerfil", "Foto de perfil actualizada en Firestore")
                fetchUserData()
            }
            .addOnFailureListener { e ->
                Log.e("actualizarFotoPerfil", "Error al actualizar la foto de perfil en Firestore: ${e.message}")
            }
    }

    fun actualizarNombre(nuevoNombre: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).update("name", nuevoNombre)
            .addOnSuccessListener {
                Log.d("actualizarNombre", "Nombre actualizado en Firestore")
                fetchUserData()
            }
            .addOnFailureListener { e ->
                Log.e("actualizarNombre", "Error al actualizar el nombre en Firestore: ${e.message}")
            }
    }
    fun actualizarPassword(nuevaPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(nuevaPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("actualizarPassword", "Contraseña actualizada en Firebase Authentication")
                db.collection("users").document(user.uid).update("password", nuevaPassword)
                    .addOnSuccessListener {
                        Log.d("actualizarPassword", "Contraseña actualizada en Firestore")
                        fetchUserData()
                    }
                    .addOnFailureListener { e ->
                        Log.e("actualizarPassword", "Error actualizando la contraseña en Firestore: ${e.message}")
                    }
            } else {
                Log.e("actualizarPassword", "Error actualizando la contraseña en Firebase Authentication: ${task.exception?.message}")
            }
        }
    }


}
