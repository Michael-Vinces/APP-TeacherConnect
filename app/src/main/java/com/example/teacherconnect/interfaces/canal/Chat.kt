package com.example.teacherconnect.interfaces.canal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teacherconnect.R
import com.example.teacherconnect.firebase.Mensajes
import com.example.teacherconnect.firebase.Usuarios
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberImagePainter
import com.example.teacherconnect.LocalBackgroundColor
import com.example.teacherconnect.LocalBorderColor
import com.example.teacherconnect.LocalTextColor
import com.example.teacherconnect.firebase.Canales
import com.example.teacherconnect.firebase.Imagenes
import com.example.teacherconnect.navegacion.Pantallas

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(navController: NavController, canalId: String?) {


    val textColors = LocalTextColor.current
    val backgroundColor = LocalBackgroundColor.current
    val borderColor = LocalBorderColor.current

    val showDialogFotoPerfil = remember { mutableStateOf(false) }
    val showDialogNombre = remember { mutableStateOf(false) }
    var showDialogConfirmDelete by remember { mutableStateOf(false) }
    var showDialogConfirmExit by remember { mutableStateOf(false) }

    var showDialogDescripcion = remember { mutableStateOf(false)}

    val textColor = LocalTextColor.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val showDialogOptions = rememberSaveable { mutableStateOf(false) }

    val showDialogParticipantes = remember { mutableStateOf(false) }

    val canalViewModel = CanalViewModel()

    val chatViewModel = ChatViewModel()

    val user = FirebaseAuth.getInstance().currentUser?.uid?.let { canalViewModel.obtenerUsuarioPorId(it) }

    val lol = FirebaseAuth.getInstance().currentUser?.uid

    val sape by canalViewModel.obtenerUsuarioPorId(lol).observeAsState()

    val logMessage = sape?.occupation
    Log.d("sape zapato", logMessage.toString())

    //val usuario =
    //    user?.value?.id?.let { CanalViewModel().obtenerUsuarioPorId(it) } //esta linea obtiene el usuario segun la id que se le pasa y la guarda en una variable

    val canal: Canales? by chatViewModel.obtenerCanalPorId(canalId).observeAsState(null)

    /////////val sape: Usuarios? by canalViewModel.obtenerUsuarioPorId()

    //val usuario: Usuarios? by CanalViewModel().obtenerUsuarioPorId(mensaje.usuarioID).observeAsState(null)



    canal?.let { chatViewModel.fetchFotoPerfil(it.imagenId) }

    chatViewModel.obtenerMensajesPorCanal(canalId)

    val selectedImage = rememberSaveable { mutableStateOf(-1) }

    val imagenesFotoPerfil: List<Imagenes> by chatViewModel.obtenerImagenesFotoPerfil().observeAsState(listOf())

    LaunchedEffect(Unit){
        chatViewModel.obtenerMensajesPorCanal(canalId)
    }

    val mensajesList by chatViewModel.mensajes.observeAsState(initial = listOf())

    val urlImagen by chatViewModel.urlFotoPerfil.observeAsState(initial = null)

    var mensajeEnviado by remember { mutableStateOf("") }

    val nameChange = rememberSaveable { mutableStateOf("") }

    val descripcionChange = rememberSaveable { mutableStateOf("") }




    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                })
            }
    ) {

        // 1. Fondo de la pantalla
        Image(
            painter = painterResource(id = R.drawable.fondo_chat),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Barra superior
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberImagePainter(data = urlImagen ?: ""),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color = Color.White)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    canal?.let {
                        Text(text = it.nombreCanal)
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = {navController.navigate(Pantallas.TusCanalesConexion.name)}) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = {showDialogOptions.value=true}) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
            }
        )

        if (showDialogOptions.value) {
            AlertDialog(
                onDismissRequest = {
                    showDialogOptions.value = false
                },
                title = { Text(
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = textColors.value
                    ),
                    text = "Ajustes del canal") },
                text = {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){


                        if(sape?.occupation.toString() == "Profesor") {
                            Text(text = "Foto de perfil",
                                fontWeight = FontWeight.Normal,
                                color = textColor.value)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = backgroundColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        2.dp,
                                        color = borderColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .height(80.dp)
                                    .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = urlImagen ?: ""),
                                    contentDescription = "Imagen de perfil",
                                    modifier = Modifier
                                        .size(100.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    painter = rememberImagePainter(data = R.drawable.icon_editar),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            showDialogFotoPerfil.value = true
                                            showDialogOptions.value = false
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(text = "Nombre",
                                fontWeight = FontWeight.Normal,
                                color = textColor.value)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = backgroundColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        2.dp,
                                        color = borderColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .height(80.dp)
                                    .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(40.dp))
                                canal?.let {
                                    Text(
                                        text = it.nombreCanal,
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            color = textColor.value
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    painter = rememberImagePainter(data = R.drawable.icon_editar),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            showDialogNombre.value = true
                                        }
                                )

                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(text = "Descripción",
                                fontWeight = FontWeight.Normal,
                                color = textColor.value)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = backgroundColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        2.dp,
                                        color = borderColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .height(80.dp)
                                    .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(40.dp))
                                canal?.let {
                                    Text(
                                        text = it.descripcion,
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            color = textColor.value
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    painter = rememberImagePainter(data = R.drawable.icon_editar),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            showDialogDescripcion.value = true
                                        }
                                )

                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(text = "Pin del Canal",
                                fontWeight = FontWeight.Normal,
                                color = textColor.value)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = backgroundColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        2.dp,
                                        color = borderColor.value,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .height(80.dp)
                                    .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(40.dp))
                                canal?.let {
                                    Text(
                                        text = it.pin,
                                        style = TextStyle(
                                            fontSize = 15.sp,
                                            color = textColor.value
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    painter = rememberImagePainter(data = R.drawable.icon_editarblock),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(40.dp)
                                )

                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Button(onClick = {
                                showDialogConfirmDelete = true
                            }) {
                                Text("Eliminar canal")
                            }
                        }



                        Spacer(modifier = Modifier.height(15.dp))

                        Button(onClick = {
                            showDialogParticipantes.value = true
                        }) {
                            Text("Lista de participantes")
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        if(sape?.occupation.toString() == "Estudiante") {

                            Button(onClick = {
                                showDialogConfirmExit = true
                            }) {
                                Text("Salir del canal")
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
                            showDialogOptions.value = false

                        }) {
                            Text("Regresar")
                        }
                    }
                },
                containerColor=backgroundColor.value,
                shape = RoundedCornerShape(8.dp),
                modifier=Modifier
                    .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
            )
        }

        if (showDialogParticipantes.value) {
            AlertDialog(
                onDismissRequest = { showDialogParticipantes.value = false },
                title = {
                    Text(
                        style = TextStyle(
                            fontSize = 22.sp,
                            color = textColor.value
                        ),
                        text = "Participantes del canal"
                    )
                },
                text = {
                    val usuarios = chatViewModel.obtenerUsuariosDelCanal(canalId).observeAsState(initial = emptyList())

                    LazyColumn {
                        val profesor = usuarios.value.find { it.occupation.toString() == "Profesor" }
                        profesor?.let {
                            item {
                                Text(text = "Profesor:", style = TextStyle(fontWeight = FontWeight.Bold, color = textColor.value))
                                Text(text = it.name, color = textColor.value)
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }

                        item {
                            Text(text = "Estudiantes:", style = TextStyle(fontWeight = FontWeight.Bold, color = textColor.value))
                        }

                        val estudiantes = usuarios.value.filter { it.occupation.toString() == "Estudiante" }
                        items(estudiantes) { estudiante ->
                            Text(text = estudiante.name, color = textColor.value)
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
                            showDialogParticipantes.value = false
                        }) {
                            Text("Regresar")
                        }
                    }
                },
                containerColor = backgroundColor.value,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .border(2.dp, color = borderColor.value, RoundedCornerShape(8.dp))
            )
        }

        if (showDialogConfirmDelete) {
            AlertDialog(
                onDismissRequest = {
                    showDialogConfirmDelete = false
                },
                title = { Text("Confirmación") },
                text = { Text("¿Estás seguro de que quieres eliminar este canal?") },
                confirmButton = {
                    Button(onClick = {

                        chatViewModel.eliminarCanal(canalId,
                            onSuccess = {
                                navController.navigate(Pantallas.TusCanalesConexion.name)
                                showDialogConfirmDelete = false
                                showDialogOptions.value = false
                            },
                            onFailure = { exception ->
                                showDialogConfirmDelete = false
                            }
                        )

                        //
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialogConfirmDelete = false
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showDialogConfirmExit) {
            AlertDialog(
                onDismissRequest = {
                    showDialogConfirmExit = false
                },
                title = { Text("Confirmación") },
                text = { Text("¿Estás seguro de que quieres salir de este canal?") },
                confirmButton = {
                    Button(onClick = {

                        chatViewModel.exitCanal(canalId,
                            onSuccess = {
                                navController.navigate(Pantallas.TusCanalesConexion.name)
                                showDialogConfirmExit = false
                                showDialogOptions.value = false
                            },
                            onFailure = { exception ->
                                showDialogConfirmExit = false
                            }
                        )

                        //
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialogConfirmExit = false
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showDialogFotoPerfil.value) {
            AlertDialog(
                onDismissRequest = { showDialogFotoPerfil.value = false },
                title = { Text(
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = textColor.value
                    ),
                    text = "Foto de Perfil:") },
                text = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(
                                painter = rememberImagePainter(data = urlImagen ?: ""),
                                contentDescription = "Imagen de perfil",
                                modifier = Modifier
                                    .size(100.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(50.dp)
                            )
                            if (selectedImage.value in imagenesFotoPerfil.indices) {
                                Image(
                                    painter = rememberImagePainter(data = imagenesFotoPerfil[selectedImage.value].url),
                                    contentDescription = null,
                                    modifier = Modifier.size(90.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Selecciona tu nueva foto de perfil:",
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = textColor.value,
                                fontWeight = FontWeight.Bold
                            ))
                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            items(imagenesFotoPerfil) { imagen ->
                                com.example.teacherconnect.interfaces.home.RadioButtonImageOption(
                                    imageUrl = imagen.url,
                                    isSelected = selectedImage.value == imagenesFotoPerfil.indexOf(
                                        imagen
                                    ),
                                    onSelected = {
                                        selectedImage.value = imagenesFotoPerfil.indexOf(imagen)
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
                            showDialogFotoPerfil.value = false
                            showDialogOptions.value = true

                        }) {
                            Text("Regresar")
                        }
                        Button(onClick = {
                            if (selectedImage.value in imagenesFotoPerfil.indices) {
                                val nuevaFotoPerfilId = imagenesFotoPerfil[selectedImage.value].id // Asegúrate de tener un campo 'id' en tu clase que represente la imagen.
                                nuevaFotoPerfilId?.let {
                                    chatViewModel.actualizarFotoPerfil(
                                        it, canalId
                                    )
                                }
                            }
                            showDialogFotoPerfil.value = false
                        }) {
                            Text("Guardar")
                        }
                    }
                },
                containerColor=backgroundColor.value,
                shape = RoundedCornerShape(8.dp),
                modifier=Modifier
                    .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
            )
        }

        if (showDialogNombre.value) {
            AlertDialog(
                onDismissRequest = { showDialogNombre.value = false },
                title = { Text(
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = textColor.value
                    ),
                    text = "Nombre:") },
                text = {
                    Column {
                        Text(text = "Cambio de nombre:",
                            fontWeight = FontWeight.Normal,
                            color = textColor.value)
                        TextField(
                            value = nameChange.value,
                            onValueChange = { nameChange.value = it },
                            placeholder = { canal?.let { Text(text = it.nombreCanal) } }
                        )
                        Spacer(modifier = Modifier.height(15.dp))
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
                            showDialogOptions.value = true
                            showDialogNombre.value = false
                            nameChange.value=""
                        }) {
                            Text("Regresar")
                        }
                        Button(onClick = {

                            val nuevoNombre = nameChange.value
                            nuevoNombre.let {
                                chatViewModel.actualizarNombre(it, canalId)
                            }
                            showDialogOptions.value = false
                            showDialogNombre.value = false
                            nameChange.value=""
                        }) {
                            Text("Guardar")
                        }

                    }
                },
                containerColor=backgroundColor.value,
                shape = RoundedCornerShape(8.dp),
                modifier=Modifier
                    .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
            )
        }

        if (showDialogDescripcion.value) {
            AlertDialog(
                onDismissRequest = { showDialogDescripcion.value = false },
                title = { Text(
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = textColor.value
                    ),
                    text = "Descripción:") },
                text = {
                    Column {
                        Text(text = "Cambiar descripción:",
                            fontWeight = FontWeight.Normal,
                            color = textColor.value)
                        TextField(
                            value = descripcionChange.value,
                            onValueChange = { descripcionChange.value = it },
                            placeholder = { canal?.let { Text(text = it.descripcion) } }
                        )
                        Spacer(modifier = Modifier.height(15.dp))
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
                            showDialogOptions.value = true
                            showDialogDescripcion.value = false
                            descripcionChange.value=""
                        }) {
                            Text("Regresar")
                        }
                        Button(onClick = {

                            val nuevaDescripcion = descripcionChange.value
                            nuevaDescripcion.let {
                                chatViewModel.actualizarDescripcion(it, canalId)
                            }
                            showDialogOptions.value = false
                            showDialogDescripcion.value = false
                            descripcionChange.value=""
                        }) {
                            Text("Guardar")
                        }

                    }
                },
                containerColor=backgroundColor.value,
                shape = RoundedCornerShape(8.dp),
                modifier=Modifier
                    .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
            )
        }

        // 3. Mensajes del chat

        LazyColumn(
            modifier = Modifier
                .padding(
                    top = 56.dp,
                    bottom = 80.dp
                ) // Ajusta estos valores según la altura de tus barras.
                .padding(8.dp),
            reverseLayout = true // para que los mensajes más recientes aparezcan abajo
        ) {
            items(mensajesList) { mensaje ->
                val isCurrentUser = mensaje.usuarioID == user?.value?.id
                MessageItem(mensaje, isCurrentUser, user?.value?.name ?: "")
            }
        }

        // 4. Barra inferior para chatear
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(
                            29,
                            32,
                            41
                        )
                    ) // Aquí eliges el color de fondo de la barra del chat
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* abrir selector de emojis */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.icono_chat),
                        contentDescription = "Seleccionar emoji"
                    )
                }

                TextField(
                    value = mensajeEnviado,
                    onValueChange = { newValue ->
                        mensajeEnviado = newValue
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp)
                        .background(color = Color.Black),
                    placeholder = { Text(text = "Mensaje") }
                )

                IconButton(onClick = {
                    // Guardar el mensaje en Firebase
                    val chatViewModel = ChatViewModel()

                    chatViewModel.EnviarMensaje(
                        mensajeEnviado,
                        canalId,
                        user?.value?.id,
                        onSuccess = { id ->
                            Log.d("TeacherConnect", "Mensaje enviado con éxito, ID: $id")
                        },
                        onFailure = {
                            Log.d("TeacherConnect", "Error al enviar mensaje: ${it.message}")
                        }
                    )
                    mensajeEnviado = "" // resetea el campo después de enviar
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar mensaje")
                }
            }
        }

    }
}

@Composable
fun MessageItem(mensaje: Mensajes, isCurrentUser: Boolean, currentUserName: String) {
    val maxWidth = LocalContext.current.resources.displayMetrics.widthPixels * 4 / 5
    val minWidth = LocalContext.current.resources.displayMetrics.widthPixels * 1 / 5
    val usuario: Usuarios? by CanalViewModel().obtenerUsuarioPorId(mensaje.usuarioID).observeAsState(null)
    /*val logMessage = usuario
    Log.d("MiTag", logMessage.toString())*/

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrentUser) Color.LightGray else Color.White)
                .padding(8.dp)
                .widthIn(min = minWidth.dp, max = maxWidth.dp) // Aquí puedes definir un minWidth si lo necesitas
        ) {
            Column {
                if (!isCurrentUser) {
                    Text(text = "${usuario?.name} - ${usuario?.occupation}",
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(text = "${ currentUserName } - ${usuario?.occupation}",
                        fontWeight = FontWeight.Bold)
                }

                Text(text = mensaje.contenido)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTime(mensaje.fecha),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun formatTime(timestamp: Timestamp?): String {
    val sdf = SimpleDateFormat("hh:mm a dd/MM/yyyy", Locale.getDefault())
    return sdf.format(timestamp?.toDate() ?: Date())
}