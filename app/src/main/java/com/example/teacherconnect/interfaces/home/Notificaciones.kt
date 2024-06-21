package com.example.teacherconnect.interfaces.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.teacherconnect.LocalBackgroundColor
import com.example.teacherconnect.LocalBorderColor
import com.example.teacherconnect.LocalIsDarkMode
import com.example.teacherconnect.LocalTextColor
import com.example.teacherconnect.R
import com.example.teacherconnect.firebase.Canales
import com.example.teacherconnect.firebase.Imagenes
import com.example.teacherconnect.firebase.Notificaciones
import com.example.teacherconnect.interfaces.canal.CanalViewModel
import com.example.teacherconnect.interfaces.canal.ChatViewModel
import com.example.teacherconnect.navegacion.Pantallas
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Notificaciones(navController: NavController){
    GradientBackground {
        val isDarkMode = LocalIsDarkMode.current
        val textColors = LocalTextColor.current
        val backgroundColor = LocalBackgroundColor.current
        val borderColor = LocalBorderColor.current
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val chatViewModel: ChatViewModel = viewModel()
        val canalViewModel: CanalViewModel = viewModel()
        val showDialog = remember { mutableStateOf(false) }
        val showDialogEliminar = remember { mutableStateOf(false) }
        val imagenes by canalViewModel.obtenerImagenesEmoji().observeAsState(listOf())
        val canales by canalViewModel.canales.observeAsState(listOf())
        val notificaciones by chatViewModel.noti.observeAsState(initial = emptyList())
        val notificacionesNuevas by chatViewModel.notificacionesnuevas.observeAsState(initial = listOf())
        val notificacionesLeidas by chatViewModel.notificacionesleidas.observeAsState(initial = listOf())

        LaunchedEffect(key1 = userId) {
            userId?.let {
                chatViewModel.NotificacionesDelUsuario(it)
                canalViewModel.CanalesDelUsuario(it)
                chatViewModel.cargarNotificacionesDelUsuario(userId)

            }
        }
        LaunchedEffect(key1 = notificaciones) {
            showDialog.value = notificaciones.isEmpty()
        }
        LaunchedEffect(Unit) {
            canalViewModel.obtenerImagenesEmoji()
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
            Row (
                modifier=Modifier.padding(top=70.dp)
                    .clickable { navController.navigate(Pantallas.Home_CanalConexion.name) },
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
                )
            }
            Spacer(modifier = Modifier.height(20.dp))


            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Tus \nnotificaciones", color = textColors.value, fontSize = 45.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 115.dp, start = 40.dp)
                        .align(Alignment.Start)
                )
                Image(
                    painter = painterResource(id = R.drawable.eliminar),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(60.dp)
                        .padding(end = 5.dp)
                        .clickable {
                            showDialogEliminar.value=true
                        }
                )
                if (showDialogEliminar.value) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialogEliminar.value = false
                        },
                        title = {
                            Text(
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    color = textColors.value
                                ),
                                text = "Confirmación"
                            )
                        },
                        text = {
                            Text(
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    color = textColors.value,
                                    fontWeight = FontWeight.Bold
                                ),
                                text = "¿Está seguro de eliminar todas sus notificaciones?"
                            )
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
                                    showDialogEliminar.value = false
                                }) {
                                    Text("Cancelar")
                                }
                                Button(onClick = {
                                    showDialogEliminar.value = false
                                    chatViewModel.eliminarNotificacionesDelUsuario(userId)
                                    navController.navigate(Pantallas.HomeConexion.name)
                                }) {
                                    Text("Eliminar")
                                }
                            }
                        },
                        containerColor = backgroundColor.value,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .border(2.dp, color = borderColor.value, RoundedCornerShape(8.dp))
                    )
                }
                if (showDialog.value) {
                    Log.d("Debug", "No tienes notis")

                } else {
                    LazyColumn(modifier = Modifier.padding(8.dp).weight(1f)){

                        items(notificaciones){notificacion->
                            val canal = canales.find { it.id == notificacion.canalId }
                            val estadoNotificacion = when (notificacion.id) {
                                in notificacionesNuevas -> "New"
                                in notificacionesLeidas -> "Leída"
                                else -> "Desconocido"
                            }
                            val colorEstadoNotificacion = when (estadoNotificacion) {
                                "New" -> Color.Green 
                                "Leída" -> Color.LightGray
                                else -> Color.Gray
                            }
                            canal?.let { canalActual ->
                                val imagenCanal = imagenes.find { it.id == canalActual.imagenId }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate("ChatConexion/canalId=${canalActual.id}")
                                            chatViewModel.marcarNotificacionComoLeida(userId, notificacion.id)
                                        }
                                        .border(2.dp, color=borderColor.value)
                                        .background(Color.Transparent, shape = MaterialTheme.shapes.medium)
                                        .padding(8.dp)
                                ) {
                                    Image(
                                        painter = rememberImagePainter(data = imagenCanal?.url),
                                        contentDescription = null,
                                        modifier = Modifier.size(75.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = canalActual.nombreCanal,
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = estadoNotificacion,
                                                color = colorEstadoNotificacion,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier=Modifier.padding(end = 10.dp)
                                            )
                                        }
                                        val userName by chatViewModel.getUserNameById(notificacion.usuarioId).observeAsState("Cargando...")
                                        Text(
                                            text = userName,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = notificacion.mensajeContent,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                        notificacion.fecha?.let { fecha ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End // Esto alinea la fecha a la derecha
                                            ) {
                                                Text(
                                                    text = chatViewModel.convertTimestampToString(fecha),
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    modifier=Modifier.padding(end = 10.dp)

                                                )
                                            }
                                        }
                                    }
                                }
                                Log.d("NotiItem", "${imagenCanal?.url}, ${canalActual.nombreCanal}")
                            }
                        }
                    }


                    Log.d("Notificaiones", "$notificaciones")
                    Log.d("Canales", "$canales")
                    Log.d("Imagenes", "$imagenes")

                }
            }
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog.value = false
                    },
                    title = { Text(style = TextStyle(
                        fontSize = 22.sp,
                        color = textColors.value
                    ),
                        text = "Información") },
                    text = { Text(style = TextStyle(
                        fontSize = 22.sp,
                        color = textColors.value
                    ),
                        text= "No tiene notificaciones") },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                showDialog.value = false
                                navController.navigate(Pantallas.Home_CanalConexion.name)
                            }) {
                                Text("Volver")
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



