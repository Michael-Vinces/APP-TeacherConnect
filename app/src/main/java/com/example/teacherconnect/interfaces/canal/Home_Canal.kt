package com.example.teacherconnect.interfaces.canal

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teacherconnect.R
import com.example.teacherconnect.firebase.Canales
import com.example.teacherconnect.navegacion.Pantallas
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import coil.compose.rememberImagePainter
import com.example.teacherconnect.firebase.Imagenes
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import coil.annotation.ExperimentalCoilApi
import com.example.teacherconnect.LocalBackgroundColor
import com.example.teacherconnect.LocalBackgroundGradient
import com.example.teacherconnect.LocalBorderColor
import com.example.teacherconnect.LocalIsDarkMode
import com.example.teacherconnect.LocalTextColor
import com.example.teacherconnect.firebase.Usuarios
import com.example.teacherconnect.interfaces.home.HomeViewModel
import com.example.teacherconnect.interfaces.horario.HorarioViewModel

@Composable
fun BackGroundGradient(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val gradientColors = LocalBackgroundGradient.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors.value)
            )
    ) {
        content()
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun Home_CanalScreen(navController: NavController) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val canalName = rememberSaveable { mutableStateOf("") }
    val canalviewmodel = CanalViewModel()
    val auth = FirebaseAuth.getInstance()
    val selectedImage = rememberSaveable { mutableStateOf(-1) }
    val imagenesEmoji: List<Imagenes> by canalviewmodel.obtenerImagenesEmoji().observeAsState(listOf())
    val showDialogUnirte = rememberSaveable { mutableStateOf(false) }
    val showDialogUnirSuccess = rememberSaveable { mutableStateOf(false) }
    val canalPin = rememberSaveable { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val user: Usuarios? by canalviewmodel.obtenerUsuarioPorId(userId ?: "").observeAsState(null)
    val pin = rememberSaveable { mutableStateOf(generateRandomPin()) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isDarkMode = LocalIsDarkMode.current
    val textColors = LocalTextColor.current
    val backgroundColor = LocalBackgroundColor.current
    val borderColor = LocalBorderColor.current
    val homeViewModel= HomeViewModel()
    val tieneNotificaciones = remember { mutableStateOf(true) }

    BackGroundGradient {
        LaunchedEffect(key1 = Unit) {
            val usuarioId = auth.currentUser?.uid ?: return@LaunchedEffect
            homeViewModel.NotificacionesCheck(usuarioId) { tiene ->
                tieneNotificaciones.value = tiene
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterResource(id = if (isDarkMode.value) R.drawable.logo_blanco else R.drawable.logo_negro),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 17.dp, start = 30.dp)
                    .width(230.dp)
                    .height(170.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                Row (
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(modifier = Modifier.width(35.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowBackIos,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Text(text = "Volver", color = textColors.value, fontSize = 18.sp,
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .clickable { navController.navigate(Pantallas.HomeConexion.name) }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.icono_canales),
                            contentDescription = null,
                            modifier = Modifier
                                .size(130.dp)
                                .clickable {
                                    navController.navigate(Pantallas.TusCanalesConexion.name)
                                }
                        )
                        Text(
                            text = "Tus canales",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(60.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Image(
                            painter = painterResource(id =   if (!tieneNotificaciones.value) R.drawable.sinnotificaciones else R.drawable.nuevanotificacion),
                            contentDescription = null,
                            modifier = Modifier
                                .size(130.dp)
                                .clickable {
                                    navController.navigate(Pantallas.NotificacionesConexion.name)
                                }
                        )
                        Text(
                            text = "Tus avisos",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    if (user?.occupation == "Profesor") {
                        Image(
                            painter = painterResource(id = R.drawable.icono_nuevo_canal),
                            contentDescription = null,
                            modifier = Modifier
                                .size(130.dp)
                                .clickable {
                                    showDialog.value = true
                                }
                        )
                        Text(
                            text = "Crear un nuevo canal",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    } else if (user?.occupation == "Estudiante") {
                        Image(
                            painter = painterResource(id = R.drawable.icon_unirte_canal),
                            contentDescription = null,
                            modifier = Modifier
                                .size(130.dp)
                                .clickable {
                                    showDialogUnirte.value = true
                                }
                        )
                        Text(
                            text = "Unirte a un canal",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            if (showDialogUnirSuccess.value) {
                AlertDialog(
                    onDismissRequest = {
                        showDialogUnirSuccess.value = false
                    },
                    title = { Text(
                        style = TextStyle(
                            fontSize = 22.sp,
                            color = textColors.value
                        ),
                        text = "PIN:") },

                    text = {
                        Text(
                            style = TextStyle(
                                fontSize = 22.sp,
                                color = textColors.value
                            ),
                            text = "Se unió correctamente al canal.")

                    },
                    confirmButton = {
                        Button(onClick = {
                            showDialogUnirSuccess.value = false
                        }) {
                            Text("Salir")
                        }
                    },
                    containerColor=backgroundColor.value,
                    shape = RoundedCornerShape(8.dp),
                    modifier=Modifier
                        .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
                )
            }

            if (showDialogUnirte.value) {
                AlertDialog(
                    onDismissRequest = {
                        showDialogUnirte.value = false
                        errorMessage.value = null
                    },
                    title = { Text(
                        style = TextStyle(
                            fontSize = 22.sp,
                            color = textColors.value
                        ),
                        text = "Unirte a un canal") },
                    text = {
                        Column {
                            TextField(
                                value = canalPin.value,
                                onValueChange = {
                                    canalPin.value = it
                                    errorMessage.value = null
                                },
                                label = { Text(
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        color = textColors.value
                                    ),
                                    text= "Pin del canal") }
                            )
                            errorMessage.value?.let {
                                Text(it, color = if(isDarkMode.value) Color.Yellow else Color.Red, modifier = Modifier.padding(top = 8.dp))
                            }
                        }

                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                showDialogUnirte.value = false
                                canalPin.value = ""
                                errorMessage.value = null

                            }) {
                                Text("Cancelar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                canalviewmodel.unirUsuarioACanalPorPin(
                                    pin = canalPin.value,
                                    onSuccess = {
                                        showDialogUnirte.value = false
                                        canalPin.value = ""
                                        showDialogUnirSuccess.value = true
                                    },
                                    onFailure = { e ->
                                        errorMessage.value = "El pin que ingresaste no existe."
                                        Log.d("Error", "Error al unirse al canal: ${e.message}")
                                    }
                                )
                            }) {
                                Text("Unirte")
                            }
                        }
                    },
                    containerColor=backgroundColor.value,
                    shape = RoundedCornerShape(8.dp),
                    modifier=Modifier
                        .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
                )
            }
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(
                        style = TextStyle(
                            fontSize = 22.sp,
                            color = textColors.value
                        ),
                        text = "Crear un nuevo canal") },
                    text = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                if (selectedImage.value in imagenesEmoji.indices) {
                                    Image(
                                        painter = rememberImagePainter(data = imagenesEmoji[selectedImage.value].url),
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                TextField(
                                    value = canalName.value,
                                    onValueChange = { canalName.value = it },
                                    label = { Text("Nombre del canal") }
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "PIN: ${pin.value}",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        color = textColors.value,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    pin.value = generateRandomPin()
                                }) {
                                    Text("Regenerar PIN")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Selecciona el emoji de tu canal:",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = textColors.value,
                                    fontWeight = FontWeight.Bold
                                ))
                            LazyRow(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                items(imagenesEmoji) { imagen ->
                                    RadioButtonImageOption(
                                        imageUrl = imagen.url,
                                        isSelected = selectedImage.value == imagenesEmoji.indexOf(
                                            imagen
                                        ),
                                        onSelected = {
                                            selectedImage.value = imagenesEmoji.indexOf(imagen)
                                        }
                                    )
                                }


                            }
                        }
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                showDialog.value = false
                                canalName.value = ""
                            }) {
                                Text("Cancelar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val usuarioId = auth.currentUser?.uid ?: return@Button
                                if (selectedImage.value in imagenesEmoji.indices) {
                                    val imagenId = imagenesEmoji[selectedImage.value].id ?: ""

                                    val canal = Canales(
                                        id = null,
                                        nombreCanal = canalName.value,
                                        profesorId = usuarioId,
                                        imagenId = imagenId,
                                        pin = pin.value,
                                        descripcion="Canal ${canalName.value}"
                                    )
                                    canalviewmodel.createCanal(
                                        canal,
                                        onSuccess = { id ->
                                            Log.d("TeacherConnect", "Canal creado: $id")
                                            canalviewmodel.addCanalToUser(usuarioId, id)
                                            canalName.value = ""
                                            showDialog.value = false
                                        },
                                        onFailure = {
                                            Log.d("TeacherConnect", "Error al crear canal")
                                        }
                                    )
                                } else {
                                    Log.d("TeacherConnect", "No se ha seleccionado ninguna imagen")
                                }
                            }) {
                                Text("Crear")
                            }
                        }
                    },
                    containerColor=backgroundColor.value,
                    shape = RoundedCornerShape(8.dp),
                    modifier=Modifier
                        .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun RadioButtonImageOption(
    imageUrl: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier.clickable(onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberImagePainter(data = imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(75.dp)
                .background(if (isSelected) Color.Gray.copy(alpha = 0.3f) else Color.Transparent)
        )
    }
}

fun generateRandomPin(): String {
    val allowedChars = ('A'..'Z') + ('0'..'9')
    return (1..7)
        .map { allowedChars.random() }
        .joinToString("")
}

