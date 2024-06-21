package com.example.teacherconnect.interfaces.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teacherconnect.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextField
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import coil.annotation.ExperimentalCoilApi
import com.example.teacherconnect.navegacion.Pantallas
import coil.compose.rememberImagePainter
import com.example.teacherconnect.LocalBackgroundColor
import com.example.teacherconnect.LocalBackgroundGradient
import com.example.teacherconnect.LocalBorderColor
import com.example.teacherconnect.LocalIsDarkMode
import com.example.teacherconnect.LocalTextColor
import com.example.teacherconnect.firebase.Imagenes

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(navController: NavController){
    BackGroundGradient {
        val homeviewModel: HomeViewModel = viewModel()
        val nameChange = rememberSaveable { mutableStateOf("") }
        val passwordvalidate = rememberSaveable { mutableStateOf("") }
        val newpassword1 = rememberSaveable { mutableStateOf("") }
        val newpassword2 = rememberSaveable { mutableStateOf("") }
        val showError = remember { mutableStateOf(false) }
        val showErrorSeguridad = remember { mutableStateOf(false) }
        val showInputs = remember { mutableStateOf(false) }
        val showErrorInputs = remember { mutableStateOf(false) }
        val usuario by homeviewModel.usuario.observeAsState()
        val urlFotoPerfil by homeviewModel.urlFotoPerfil.observeAsState()
        val textColor = LocalTextColor.current
        val isDarkMode = LocalIsDarkMode.current
        val backgroundGradient = LocalBackgroundGradient.current
        val backgroundColor = LocalBackgroundColor.current
        val borderColor = LocalBorderColor.current
        val showDialogCuenta = remember { mutableStateOf(false) }
        val showDialogFotoPerfil = remember { mutableStateOf(false) }
        val showDialogNombre = remember { mutableStateOf(false) }
        val showDialogSeguridad = remember { mutableStateOf(false) }
        val imagenesFotoPerfil: List<Imagenes> by homeviewModel.obtenerImagenesFotoPerfil().observeAsState(listOf())
        val selectedImage = rememberSaveable { mutableStateOf(-1) }


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
                modifier= Modifier
                    .padding(top = 100.dp)
                    .clickable { navController.navigate(Pantallas.HomeConexion.name) },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(modifier = Modifier.width(35.dp))
                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Text(text = "Volver", color = textColor.value, fontSize = 18.sp,
                    modifier = Modifier
                        .padding(start = 3.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Configuración", color = textColor.value, fontSize = 45.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 115.dp, start = 30.dp)
                        .align(Alignment.Start)
                )
                usuario?.let { user ->
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    2.dp,
                                    color = borderColor.value,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .background(
                                    color = backgroundColor.value,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(bottom = 30.dp, top = 10.dp),
                            color = backgroundColor.value
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = urlFotoPerfil ?: ""),
                                    contentDescription = "Imagen de perfil",
                                    modifier = Modifier
                                        .size(120.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = user.name,
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        color = textColor.value
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = user.occupation,
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            color = if(isDarkMode.value) Color.LightGray else Color.DarkGray
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(27.dp)
                                            .background(if (isDarkMode.value) Color.LightGray else Color.DarkGray)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = user.email,
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            color = if(isDarkMode.value) Color.LightGray else Color.DarkGray
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
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
                                .clickable {
                                    showDialogCuenta.value = true
                                }
                                .height(56.dp)
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_cuenta),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                text = "Cuenta",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = textColor.value,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
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
                                .height(56.dp)
                                .clickable {
                                    showDialogSeguridad.value=true
                                }
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_seguridad),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                text = "Privacidad",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = textColor.value,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
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
                                .height(56.dp)
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                                .clickable {
                                    if (isDarkMode.value) {
                                        backgroundGradient.value = listOf(
                                            Color(0xFFCBCBD1),
                                            Color(0xFF2D5A84)
                                        )
                                        borderColor.value = Color(0xFF2D5A84)
                                        backgroundColor.value = Color.White
                                        textColor.value = Color.Black
                                    } else {
                                        backgroundGradient.value = listOf(
                                            Color(0xFF0E0A0B),
                                            Color(0xFF495765)
                                        )
                                        borderColor.value = Color.LightGray
                                        backgroundColor.value = Color.DarkGray
                                        textColor.value = Color.White
                                    }
                                    isDarkMode.value = !isDarkMode.value

                                },
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = if (isDarkMode.value) painterResource(id = R.drawable.icon_claro) else painterResource(id = R.drawable.icon_oscuro),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                text = "Apariencia",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = textColor.value,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(70.dp))

                    }
                    if (showDialogCuenta.value) {
                        AlertDialog(
                            onDismissRequest = {
                                showDialogCuenta.value = false
                            },
                            title = { Text(style = TextStyle(
                                fontSize = 22.sp,
                                color = textColor.value
                            ),
                                text = "Cuenta") },
                            text = {
                                Column (
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
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
                                            painter = rememberImagePainter(data = urlFotoPerfil ?: ""),
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
                                                    showDialogCuenta.value = false
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
                                        Text(
                                            text = user.name,
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                color = textColor.value
                                            )
                                        )
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
                                    Text(text = "Correo",
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
                                        Text(
                                            text = user.email,
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                color = textColor.value
                                            )
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Image(
                                            painter = rememberImagePainter(data = R.drawable.icon_editarblock),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(text = "Ocupación",
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
                                        Text(
                                            text = user.occupation,
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                color = textColor.value
                                            )
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Image(
                                            painter = rememberImagePainter(data = R.drawable.icon_editarblock),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(text = "Canales",
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
                                            .padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
                                            .clickable {
                                                navController.navigate(Pantallas.TusCanalesConexion.name)
                                            },
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(modifier = Modifier.width(40.dp))

                                        Text(text = "Número de canales: ${homeviewModel.numeroDeCanales.value}",
                                            fontWeight = FontWeight.Normal,
                                            color = textColor.value)
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            imageVector = Icons.Default.ArrowForwardIos,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(onClick = {
                                        showDialogCuenta.value = false
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
                                            painter = rememberImagePainter(data = urlFotoPerfil ?: ""),
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
                                            RadioButtonImageOption(
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
                                        showDialogCuenta.value = true

                                    }) {
                                        Text("Regresar")
                                    }
                                    Button(onClick = {
                                        if (selectedImage.value in imagenesFotoPerfil.indices) {
                                            val nuevaFotoPerfilId = imagenesFotoPerfil[selectedImage.value].id // Asegúrate de tener un campo 'id' en tu clase que represente la imagen.
                                            nuevaFotoPerfilId?.let {
                                                homeviewModel.actualizarFotoPerfil(
                                                    it
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
                                        placeholder = { Text(text = user.name) }
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(text = "Escriba su contraseña:",
                                        fontWeight = FontWeight.Normal,
                                        color = textColor.value)
                                    TextField(
                                        value = passwordvalidate.value,
                                        onValueChange = { passwordvalidate.value = it },
                                        label = { Text("Contraseña") }
                                    )
                                    if (showError.value) {
                                        Text(text = "La contraseña no coincide", color = Color.Red)
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
                                        showDialogCuenta.value = true
                                        showDialogNombre.value = false
                                        passwordvalidate.value=""
                                        nameChange.value=""
                                    }) {
                                        Text("Regresar")
                                    }
                                    Button(onClick = {
                                        if (passwordvalidate.value == user.password) {
                                            val nuevoNombre = nameChange.value
                                            nuevoNombre.let {
                                                homeviewModel.actualizarNombre(it)
                                            }
                                            showDialogCuenta.value = false
                                            showDialogNombre.value = false
                                            showError.value = false
                                            passwordvalidate.value=""
                                            nameChange.value=""
                                        } else {
                                            showError.value = true
                                        }
                                    },
                                            enabled = nameChange.value.isNotEmpty() && passwordvalidate.value.isNotEmpty()

                                    ) {
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
                    if (showDialogSeguridad.value) {
                        AlertDialog(
                            onDismissRequest = { showDialogSeguridad.value = false },
                            title = { Text(
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    color = textColor.value
                                ),
                                text = "Cambio de contraseña:") },
                            text = {
                                Column {
                                    Text(text = "Escriba su contraseña:",
                                        fontWeight = FontWeight.Normal,
                                        color = textColor.value)
                                    TextField(
                                        value = passwordvalidate.value,
                                        onValueChange = { passwordvalidate.value = it },
                                        label = { Text("Contraseña") }
                                    )
                                    Button(onClick = {
                                        if(passwordvalidate.value==user.password){
                                            showErrorSeguridad.value=false
                                            showInputs.value=true
                                        }else{
                                            showErrorSeguridad.value=true
                                        }
                                    }) {
                                        Text("Validar")
                                    }
                                    if (showErrorSeguridad.value) {
                                        Text(text = "La contraseña es incorrecta", color = Color.Red)
                                    }
                                    Spacer(modifier = Modifier.height(15.dp))
                                    val passwordLengthError = rememberSaveable { mutableStateOf(false) }
                                    val passwordMatchError = rememberSaveable { mutableStateOf(false) }

                                    if(showInputs.value) {
                                        TextField(
                                            value = newpassword1.value,
                                            onValueChange = {
                                                newpassword1.value = it
                                                passwordLengthError.value = it.length < 6
                                                passwordMatchError.value = newpassword2.value.isNotEmpty() && newpassword2.value != it
                                            },
                                            label = { Text("Contraseña Nueva") },
                                            isError = passwordLengthError.value
                                        )
                                        Spacer(modifier = Modifier.height(15.dp))
                                        TextField(
                                            value = newpassword2.value,
                                            onValueChange = {
                                                newpassword2.value = it
                                                passwordLengthError.value = it.length < 6
                                                passwordMatchError.value = newpassword1.value.isNotEmpty() && newpassword1.value != it
                                            },
                                            label = { Text("Repita la contraseña nueva") },
                                            isError = passwordLengthError.value || passwordMatchError.value
                                        )
                                        if(passwordLengthError.value) {
                                            Text(text = "La contraseña debe tener 6 o más caracteres.", color = Color.Red)
                                        } else if(showErrorInputs.value && passwordMatchError.value) {
                                            Text(text = "Las contraseñas no coinciden", color = Color.Red)
                                        }
                                    }                                }
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
                                        showDialogSeguridad.value = false
                                        newpassword1.value=""
                                        newpassword2.value=""
                                        passwordvalidate.value=""
                                        showInputs.value=false
                                    }) {
                                        Text("Regresar")
                                    }
                                    // Estado para manejar la habilitación del botón de guardar
                                    val isSaveEnabled = remember(newpassword1.value, newpassword2.value, passwordvalidate.value) {
                                        newpassword1.value.length >= 6 &&
                                                newpassword2.value == newpassword1.value &&
                                                passwordvalidate.value.length >= 6
                                    }

                                    Button(
                                        onClick = {
                                            homeviewModel.actualizarPassword(newpassword1.value)
                                            showDialogSeguridad.value = false
                                            showErrorInputs.value = false
                                            showInputs.value = false
                                            newpassword1.value = ""
                                            newpassword2.value = ""
                                            passwordvalidate.value = ""
                                        },
                                        enabled = isSaveEnabled
                                    ) {
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
                }
            }
            Image(
                painter = painterResource(id = if (isDarkMode.value) R.drawable.logo_blanco else R.drawable.logo_negro),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(130.dp)
                    .padding(bottom = 15.dp, end = 20.dp)
            )
        }
    }
}
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
                .size(100.dp)
                .background(if (isSelected) Color.Gray.copy(alpha = 0.3f) else Color.Transparent)
        )
    }
}



