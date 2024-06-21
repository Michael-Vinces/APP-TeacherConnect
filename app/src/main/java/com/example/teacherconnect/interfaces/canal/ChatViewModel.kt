package com.example.teacherconnect.interfaces.canal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teacherconnect.firebase.Canales
import com.example.teacherconnect.firebase.Mensajes
import com.example.teacherconnect.firebase.Usuarios
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import com.example.teacherconnect.firebase.Imagenes
import com.example.teacherconnect.firebase.Notificaciones
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
class ChatViewModel : ViewModel() {
    val canalViewModel = CanalViewModel()

    fun obtenerCanalPorId(canaId: String?): LiveData<Canales?> {
        val liveData = MutableLiveData<Canales?>()
        val db = FirebaseFirestore.getInstance()
        canaId?.let {
            db.collection("canales")
                .document(it)
                .get()
                .addOnSuccessListener { snapshot ->
                    val channel = snapshot.toObject(Canales::class.java)
                    liveData.value = channel
                }
        }
        return liveData
    }


    private val _mensajes = MutableLiveData<List<Mensajes>>(emptyList())

    val mensajes: LiveData<List<Mensajes>> = _mensajes

    fun obtenerMensajesPorCanal(canalId: String?, lastMessageDate: Timestamp? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            var query = db.collection("mensajes")
                .whereEqualTo("canalID", canalId)
                .orderBy("fecha", Query.Direction.DESCENDING)

            // Si se proporciona una fecha, carga mensajes anteriores a esa fecha
            lastMessageDate?.let {
                query = query.whereLessThan("fecha", it)
            }

            try {
                val snapshots = query.get().await() // Usando await() para esperar el resultado
                val nuevosMensajes = snapshots.toObjects(Mensajes::class.java)
                val combinedList = nuevosMensajes + (_mensajes.value ?: listOf())
                _mensajes.postValue(combinedList) // Actualizando el MutableLiveData
            } catch (e: Exception) {
                // Manejo de errores si es necesario
                Log.e("MiTag", "Error al obtener mensajes", e)
            }
        }
    }
    fun EnviarMensaje(
        contenido: String,
        canalId: String?,
        userId: String?,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        val nuevoMensaje = Mensajes(
            id = null,
            canalID = canalId ?: "",
            usuarioID = userId ?: "",
            contenido = contenido,
            fecha = Timestamp.now(),
            tipo = "mensaje",
            imagenID = null
        )

        db.collection("mensajes")
            .add(nuevoMensaje.toMap())
            .addOnSuccessListener { documentReference ->
                val mensajeId = documentReference.id

                db.collection("mensajes").document(mensajeId)
                    .update("id", mensajeId)
                    .addOnSuccessListener {
                        canalId?.let { id ->
                            db.collection("canales").document(id).get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val canal = documentSnapshot.toObject(Canales::class.java)
                                    val estudiantes = canal?.estudiantes?.filter { it != userId } ?: listOf()
                                    val destinatarios = mutableListOf<String>().apply {
                                        addAll(estudiantes)
                                        canal?.profesorId?.let { profesorId ->
                                            if (profesorId != userId) this.add(profesorId)
                                        }
                                    }

                                    val nuevaNotificacion = Notificaciones(
                                        id = null,
                                        canalId = canalId,
                                        usuarioId = userId ?: "",
                                        mensajeContent = contenido,
                                        fecha = Timestamp.now(),
                                    )

                                    val notiRef = db.collection("notificaciones").document()
                                    val notiId = notiRef.id
                                    nuevaNotificacion.id = notiId

                                    notiRef.set(nuevaNotificacion.toMap())
                                        .addOnSuccessListener {
                                            // Añade la notificación a cada destinatario
                                            destinatarios.forEach { usuarioId ->
                                                addNotiToUser(usuarioId, notiId)
                                            }

                                            // Agrega el mensaje al canal
                                            addMensajeToCanal(mensajeId, canalId)

                                            // Notifica que el mensaje fue enviado con éxito
                                            onSuccess(mensajeId)
                                        }
                                        .addOnFailureListener(onFailure)
                                }
                                .addOnFailureListener(onFailure)
                        }
                    }
                    .addOnFailureListener(onFailure)
            }
            .addOnFailureListener(onFailure)
    }

    fun addNotiToUser(userId: String, notificacionId: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userRef.update("notificaciones", FieldValue.arrayUnion(notificacionId))
            .addOnSuccessListener {
                Log.d("TeacherConnect", "Notificación añadida al usuario $userId")
            }
            .addOnFailureListener {
                Log.d("TeacherConnect", "Error añadiendo notificación: $it")
            }
    }
    private val _notificacionesNuevas = MutableLiveData<List<String>>()
    val notificacionesnuevas: LiveData<List<String>> = _notificacionesNuevas

    private val _notificacionesLeidas = MutableLiveData<List<String>>()
    val notificacionesleidas: LiveData<List<String>> = _notificacionesLeidas


    fun cargarNotificacionesDelUsuario(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                _notificacionesNuevas.value = document["notificaciones"] as? List<String> ?: listOf()
                _notificacionesLeidas.value = document["notificacionesleidas"] as? List<String> ?: listOf()
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error al obtener listas de notificaciones", e)
            }
    }
    fun marcarNotificacionComoLeida(userId: String?, notificacionId: String?) {
        val db = FirebaseFirestore.getInstance()
        val userRef = userId?.let { db.collection("users").document(it) }

        // Crear una transacción para asegurar la consistencia de los datos
        db.runTransaction { transaction ->
            // Eliminar la notificación de notificacionesNuevas
            userRef?.let { transaction.update(it, "notificaciones", FieldValue.arrayRemove(notificacionId)) }
            // Agregar la notificación a notificacionesLeidas
            userRef?.let { transaction.update(it, "notificacionesleidas", FieldValue.arrayUnion(notificacionId)) }
        }.addOnSuccessListener {
            Log.w("App", "Marcado notificación como leída")

        }.addOnFailureListener { e ->
            Log.w("App", "Error al marcar notificación como leída", e)
        }
    }

    fun eliminarNotificacionesDelUsuario(userId: String?) {
        val db = FirebaseFirestore.getInstance()

        val userRef = userId?.let { db.collection("users").document(it) }

        userRef?.update("notificaciones", listOf<Any>())
            ?.addOnSuccessListener {
                Log.d("App", "Todas las notificaciones han sido eliminadas.")
            }
            ?.addOnFailureListener { e ->
                Log.w("App", "Error al eliminar las notificaciones", e)
            }
    }


    val noti = MutableLiveData<List<Notificaciones>>()
    fun addMensajeToCanal(mensajesId: String, canalId: String?) {
        val canalRef = canalId?.let {
            FirebaseFirestore.getInstance().collection("canales").document(
                it
            )
        }

        canalRef?.update("mensajes", FieldValue.arrayUnion(mensajesId))
            ?.addOnSuccessListener {
                Log.d("TeacherConnect", "Mensaje añadido al canal")
            }
            ?.addOnFailureListener {
                Log.d("TeacherConnect", "Error añadiendo mensaje: ${it}")
            }
    }
    fun NotificacionesDelUsuario(userId: String) {
        // Actualizar las listas de notificaciones nuevas y leídas
        cargarNotificacionesDelUsuario(userId)

        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    val todasNotiIds = (_notificacionesNuevas.value ?: listOf()) +
                            (_notificacionesLeidas.value ?: listOf())

                    if (todasNotiIds.isNotEmpty()) {
                        db.collection("notificaciones")
                            .whereIn("id", todasNotiIds)
                            .orderBy("fecha", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val notiList = querySnapshot.documents.mapNotNull { it.toObject(Notificaciones::class.java) }
                                Log.d("Debug", "Notificaciones encontradas: $notiList")
                                noti.value = notiList
                            }
                    } else {
                        noti.value = listOf()
                    }
                }
        }
    }

    fun getUserNameById(userId: String): LiveData<String> {
        val userName = MutableLiveData<String>()

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                userName.value = document.getString("name")
            }
            .addOnFailureListener {
                Log.e("ViewModel", "Error al obtener el nombre del usuario", it)
            }

        return userName
    }
    fun convertTimestampToString(timestamp: Timestamp): String {
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date(milliseconds)
        return sdf.format(date)
    }

    private val _urlFotoPerfil = MutableLiveData<String?>(null)
    val urlFotoPerfil: LiveData<String?> get() = _urlFotoPerfil

    private val db = FirebaseFirestore.getInstance()
    fun fetchFotoPerfil(fotoPerfilId: String) {
        if (fotoPerfilId.isNotEmpty()) {
            db.collection("imagenes").document(fotoPerfilId).get().addOnSuccessListener { documentSnapshot ->
                val url = documentSnapshot.getString("url")
                _urlFotoPerfil.value = url
            }.addOnFailureListener { e ->
                Log.e("fetchFotoPerfil", "Error al obtener la foto de perfil: ${e.message}")
            }
        }
    }

    fun obtenerImagenesFotoPerfil(): LiveData<List<Imagenes>> {
        val liveData = MutableLiveData<List<Imagenes>>()
        val db = FirebaseFirestore.getInstance()
        db.collection("imagenes")
            .whereEqualTo("categoria", "emoji")
            .get()
            .addOnSuccessListener { result ->
                val images = result.map { it.toObject(Imagenes::class.java) }
                liveData.value = images
            }.addOnFailureListener { e ->
                Log.e("obtenerImagenesFotoPerfil", "Error al obtener imágenes de foto de perfil: ${e.message}")
            }
        return liveData
    }

    fun actualizarFotoPerfil(nuevaFotoPerfilId: String, canalId: String?) {
        if (canalId != null) {
            db.collection("canales").document(canalId).update("imagenId", nuevaFotoPerfilId)
                .addOnSuccessListener {
                    Log.d("actualizarFotoPerfil", "Foto de perfil actualizada en Firestore")
                    fetchFotoPerfil(nuevaFotoPerfilId)
                }
                .addOnFailureListener { e ->
                    Log.e("actualizarFotoPerfil", "Error al actualizar la foto de perfil en Firestore: ${e.message}")
                }
        }
    }

    fun actualizarNombre(nuevoNombre: String, canalId: String?) {
        if (canalId != null) {
            db.collection("canales").document(canalId).update("nombreCanal", nuevoNombre)
                .addOnSuccessListener {
                    Log.d("actualizarNombre", "Nombre actualizado en Firestore")
                    obtenerCanalPorId(canalId)
                }
                .addOnFailureListener { e ->
                    Log.e("actualizarNombre", "Error al actualizar el nombre en Firestore: ${e.message}")
                }
        }
    }

    fun actualizarDescripcion(nuevoNombre: String, canalId: String?) {
        if (canalId != null) {
            db.collection("canales").document(canalId).update("descripcion", nuevoNombre)
                .addOnSuccessListener {
                    Log.d("actualizardesc", "desc actualizado en Firestore")
                    obtenerCanalPorId(canalId)
                }
                .addOnFailureListener { e ->
                    Log.e("actualizardesc", "Error al actualizar la desc en Firestore: ${e.message}")
                }
        }
    }

    fun obtenerUsuariosDelCanal(canalId: String?): LiveData<List<Usuarios>> {
        val liveData = MutableLiveData<List<Usuarios>>()
        val db = FirebaseFirestore.getInstance()

        canalId?.let { id ->
            db.collection("canales").document(id).get().addOnSuccessListener { document ->
                val profesorId = document.getString("profesorId")
                val estudiantes = document.get("estudiantes") as? List<String> ?: listOf()

                val usuariosList = mutableListOf<Usuarios>()

                profesorId?.let {
                    canalViewModel.obtenerUsuarioPorId(it).observeForever { profesor ->
                        profesor?.let {
                            usuariosList.add(it)

                            if (usuariosList.size == estudiantes.size + 1) {
                                liveData.value = usuariosList
                            }
                        }
                    }
                }

                for (estudianteId in estudiantes) {
                    canalViewModel.obtenerUsuarioPorId(estudianteId).observeForever { estudiante ->
                        estudiante?.let {
                            usuariosList.add(it)

                            if (usuariosList.size == estudiantes.size + 1) {
                                liveData.value = usuariosList
                            }
                        }
                    }
                }
            }
        }

        return liveData
    }


    fun eliminarCanal(canalId: String?, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val canalRef = canalId?.let { db.collection("canales").document(it) }

        // Paso 1: Eliminar el documento del canal
        if (canalRef != null) {
            canalRef.delete().addOnSuccessListener {

                // Paso 2: Eliminar todos los mensajes que pertenecen a ese canal
                val mensajesRef = db.collection("mensajes")
                mensajesRef.whereEqualTo("canalID", canalId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()
                        for (document in querySnapshot.documents) {
                            batch.delete(document.reference)
                        }

                        // Ejecutar batch para eliminar todos los mensajes
                        batch.commit().addOnSuccessListener {

                            // Paso 3: Eliminar canalId de la lista "canales" de cada usuario
                            val usersRef = db.collection("users")
                            usersRef.whereArrayContains("canales", canalId)
                                .get()
                                .addOnSuccessListener { userQuerySnapshot ->
                                    val userBatch = db.batch()
                                    for (userDocument in userQuerySnapshot.documents) {
                                        userBatch.update(
                                            userDocument.reference,
                                            "canales",
                                            FieldValue.arrayRemove(canalId)
                                        )
                                    }

                                    // Ejecutar batch para actualizar usuarios
                                    userBatch.commit().addOnSuccessListener {
                                        onSuccess()
                                    }.addOnFailureListener { exception ->
                                        onFailure(exception)
                                    }
                                }.addOnFailureListener { exception ->
                                    onFailure(exception)
                                }
                        }.addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                    }.addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }
    }

    fun exitCanal(canalId: String?, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val usuarioRef = db.collection("users").document(usuarioId)
        usuarioRef.update("canales", FieldValue.arrayRemove(canalId))
            .addOnSuccessListener {

                val canalRef = canalId?.let { db.collection("canales").document(it) }
                canalRef?.update("estudiantes", FieldValue.arrayRemove(usuarioId))
                    ?.addOnSuccessListener {
                        onSuccess()
                    }
                    ?.addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}