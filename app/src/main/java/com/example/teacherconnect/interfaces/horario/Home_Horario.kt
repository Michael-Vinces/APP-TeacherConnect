package com.example.teacherconnect.interfaces.horario

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teacherconnect.LocalBackgroundColor
import com.example.teacherconnect.LocalBackgroundGradient
import com.example.teacherconnect.LocalBorderColor
import com.example.teacherconnect.LocalIsDarkMode
import com.example.teacherconnect.LocalTextColor
import com.example.teacherconnect.R
import com.example.teacherconnect.firebase.Horarios
import com.example.teacherconnect.navegacion.Pantallas
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FondoHome(
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home_HorariosScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val isDarkMode = LocalIsDarkMode.current
    val horarioviewmodel= HorarioViewModel()
    val showDialogCrear = rememberSaveable { mutableStateOf(false) }
    val showDialogEliminar = rememberSaveable { mutableStateOf(false) }
    val showDialogAviso = rememberSaveable { mutableStateOf(false) }
    val nombreHorario = rememberSaveable { mutableStateOf("") }
    val textColors = LocalTextColor.current
    val backgroundColor = LocalBackgroundColor.current
    val borderColor = LocalBorderColor.current
    val tieneHorario = remember { mutableStateOf(true) }

    FondoHome {

        LaunchedEffect(key1 = Unit) {
            val usuarioId = auth.currentUser?.uid ?: return@LaunchedEffect
            horarioviewmodel.horarioCheck(usuarioId) { tiene ->
                tieneHorario.value = tiene
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 165.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!tieneHorario.value) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { showDialogCrear.value = true }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.crear),
                        contentDescription = null,
                        modifier = Modifier
                            .width(170.dp)
                            .height(160.dp)
                    )
                    Text(
                        text = "Crear horario",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }else {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        showDialogEliminar.value = true
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_eliminarhorario),
                        contentDescription = null,
                        modifier = Modifier
                            .width(170.dp)
                            .height(160.dp)
                    )
                    Text(
                        text = "Eliminar horario",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
        Image(
            painter = painterResource(id = if (isDarkMode.value) R.drawable.logo_blanco else R.drawable.logo_negro),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset(x = (185).dp, y = (16).dp)
                .width(160.dp)
                .height(160.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box (modifier = Modifier.fillMaxSize()){
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 50.dp)
                    .clickable { navController.navigate(Pantallas.HomeConexion.name) },
                horizontalArrangement = Arrangement.Start,
            ) {
                Spacer(modifier = Modifier.width(45.dp))
                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Text(
                    text = "Volver",style = TextStyle(
                        fontSize = 18.sp,
                        color = textColors.value,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(start = 3.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (100).dp, y = (-35).dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable{
                        if (!tieneHorario.value) {
                            showDialogAviso.value=true
                        }else{
                            navController.navigate(Pantallas.FormActividadesConexion.name)
                        }
                    }
            ){
                Image(
                    painter = painterResource(id = R.drawable.mod),
                    contentDescription = null,
                    modifier = Modifier
                        .width(170.dp)
                        .height(160.dp)
                )
                Text(
                    text = "Modificar",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-100).dp, y = (-35).dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier .clickable{
                    if (!tieneHorario.value) {
                        showDialogAviso.value=true
                    }else{
                        navController.navigate(Pantallas.ActividadesConexion.name)
                    }
                }
            ){
                Image(
                    painter = painterResource(id = R.drawable.ver),
                    contentDescription = null,
                    modifier = Modifier
                        .width(170.dp)
                        .height(160.dp)
                )
                Text(
                    text = "Ver tu horario",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
    if (showDialogCrear.value) {
        AlertDialog(
            onDismissRequest = {
                showDialogCrear.value = false
            },
            title = { Text(
                style = TextStyle(
                    fontSize = 22.sp,
                    color = textColors.value
                ),
                text = "Crear un horario") },
            text = {
                Column {
                    TextField(
                        value = nombreHorario.value,
                        onValueChange = {
                            nombreHorario.value = it
                        },
                        label = { Text("Titulo del horario") }
                    )
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
                        showDialogCrear.value = false
                        nombreHorario.value = ""

                    }) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val usuarioId = auth.currentUser?.uid ?: return@Button
                        val horario = Horarios(
                            id = null,
                            titulo = nombreHorario.value,
                            usuarioid = usuarioId
                        )
                        horarioviewmodel.crearHorario(
                            horario,
                            onSuccess = { id ->
                                Log.d("TeacherConnect", "Horario creado: $id")
                                horarioviewmodel.addHorarioToUser(usuarioId, id)
                                nombreHorario.value = ""
                                showDialogCrear.value = false
                                tieneHorario.value = true
                                navController.navigate(Pantallas.FormActividadesConexion.name)
                            },
                            onFailure = {
                                Log.d("TeacherConnect", "Error al crear el horario")
                            }
                        )
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
    if (showDialogAviso.value){
        AlertDialog(
            onDismissRequest = {
                showDialogAviso.value = false
            },
            title = { Text(
                style = TextStyle(
                    fontSize = 22.sp,
                    color = textColors.value
                ),
                text = "Aviso") },
            text = {
                Text(
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = textColors.value,
                        fontWeight = FontWeight.Bold
                    ),
                    text = "Debe crear un horario para proceder a modificar o visualizar un horario")
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
                        showDialogAviso.value = false
                    }) {
                        Text("Cancelar")
                    }
                }
            },
            containerColor=backgroundColor.value,
            shape = RoundedCornerShape(8.dp),
            modifier=Modifier
                .border(2.dp, color=borderColor.value, RoundedCornerShape(8.dp))
        )
    }
    if (showDialogEliminar.value){
        AlertDialog(
            onDismissRequest = {
                showDialogEliminar.value = false
            },
            title = { Text(
                style = TextStyle(
                    fontSize = 22.sp,
                    color = textColors.value
                ),
                text = "Confirmación") },
            text = {
                Text(
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = textColors.value,
                        fontWeight = FontWeight.Bold
                    ),
                    text = "¿Está seguro de eliminar su horario?")
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
                        val usuarioId = auth.currentUser?.uid ?: return@Button
                        horarioviewmodel.eliminarHorario(
                            usuarioId,
                            onSuccess = {
                                tieneHorario.value = false
                            },
                            onFailure = { exception ->
                                Log.d("TeacherConnect", "Error eliminando el horario: ${exception}")
                            }
                        )
                    }) {
                        Text("Eliminar")
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


