package com.example.teacherconnect.interfaces.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherconnect.firebase.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class LoginViewModel: ViewModel(){
    private val auth:FirebaseAuth=Firebase.auth
    private val _loading= MutableLiveData(false)

    fun signWithEmailAndPassword(email:String,password:String, onResult: (SignInResult) -> Unit)
            = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {  task->
                    if(task.isSuccessful){
                        Log.d("Inicio","Hecho brou")
                        onResult(SignInResult.Success)
                    }
                    else {
                        val exception = task.exception
                        Log.d("ErrorFirebase", exception?.message ?: "Error sin mensaje")

                        when (exception) {
                            is FirebaseAuthInvalidUserException -> {
                                onResult(SignInResult.EmailError)
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                onResult(SignInResult.PasswordError)
                            }
                            else -> {
                                onResult(SignInResult.UnknownError)
                            }
                        }
                    }
                }
        }
        catch (ex:Exception){
            Log.d("Inicio","Error: ${ex.message}")

        }
    }
    fun createUserWithEmailAndPassword(email: String, password: String, name:String, occupation:String,home:()->Unit){
        if(_loading.value==false){
            _loading.value=true
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        createUser(email,password,name,occupation)
                        home()
                    } else {
                        Log.d("TeacherConnect", "Error: ${task.result.toString()}")
                    }
                    _loading.value=false
                }
        }
    }

    private fun createUser(email: String,password: String,name: String,occupation: String) {
        val userId=auth.currentUser?.uid

        val user=Usuarios(
            id=null,
            email= email,
            password= password,
            name= name,
            occupation= occupation,
            fotoPerfilId="UyOAxSF3hTxeHxt0t22L"
        ).toMap()
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId.toString())
        userRef.set(user.toMap()).addOnSuccessListener {
            userRef.update("id", userRef.id).addOnSuccessListener {
                Log.d("TeacherConnect", "Creado ${userId.toString()}")
            }
        }.addOnFailureListener {
            Log.d("TeacherConnect", "Error: ${it}")
        }
    }
    sealed class SignInResult {
        object Success : SignInResult()
        object PasswordError : SignInResult()
        object EmailError : SignInResult()
        object UnknownError : SignInResult()
    }
    fun logout(){
        auth.signOut()
    }
}