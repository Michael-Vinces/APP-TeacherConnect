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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import coil.annotation.ExperimentalCoilApi
import com.example.teacherconnect.firebase.Canales
import com.example.teacherconnect.navegacion.Pantallas
import coil.compose.rememberImagePainter
import com.example.teacherconnect.LocalBackgroundColor
import com.example.teacherconnect.LocalBackgroundGradient
import com.example.teacherconnect.LocalBorderColor
import com.example.teacherconnect.LocalTextColor
import com.example.teacherconnect.firebase.Imagenes

@Composable
fun BackGroundGradientCanales(
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
@Composable
fun ChannelScreen(navController: NavController){
    BackGroundGradientCanales {
        val canalViewModel: CanalViewModel = viewModel()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val showDialog = remember { mutableStateOf(false) }
        val textColors = LocalTextColor.current
        val backgroundColor = LocalBackgroundColor.current
        val borderColor = LocalBorderColor.current
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
            LaunchedEffect(key1 = userId) {
                userId?.let {
                    canalViewModel.CanalesDelUsuario(it)
                }
            }
            val imagenes by canalViewModel.obtenerImagenesEmoji().observeAsState(listOf())
            LaunchedEffect(Unit) {
                canalViewModel.obtenerImagenesEmoji()
            }
            val canales by canalViewModel.canales.observeAsState(listOf())
            LaunchedEffect(key1 = canales) {
                showDialog.value = canales.isEmpty()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Tus \ncanales", color = textColors.value, fontSize = 45.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 115.dp, start = 40.dp).align(Alignment.Start)
                )
                if (canales.isEmpty()) {
                    Log.d("canales","no hay canales")
                } else {
                    ListaDeCanales(
                        canales = canales,
                        imagenes = imagenes,
                        modifier = Modifier.weight(1f),
                        navController = navController
                    )
                    Log.d("canales","si hay canales")

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
                        text= "No tiene canales creados") },
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

@Composable
fun ListaDeCanales(canales: List<Canales>, imagenes: List<Imagenes>, modifier: Modifier = Modifier, navController : NavController) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(canales.chunked(2)) { chunkedCanales ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
            ) {
                CanalItem(chunkedCanales[0], imagenes, Modifier.weight(1f),navController = navController)
                Spacer(Modifier.width(8.dp))
                if (chunkedCanales.size > 1) {
                    CanalItem(chunkedCanales[1], imagenes, Modifier.weight(1f),navController = navController)
                }
            }
        }
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun CanalItem(canal: Canales, imagenes: List<Imagenes>, modifier: Modifier = Modifier, navController : NavController) {
    val imagenCanal = imagenes.find { it.id == canal.imagenId }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .size(140.dp)
            .background(Color.Transparent, shape = MaterialTheme.shapes.medium)
            .clickable {
                navController.navigate("ChatConexion/canalId=${canal.id}")
            }
    ) {
        Image(
            painter = rememberImagePainter(data = imagenCanal?.url),
            contentDescription = null,
            modifier = Modifier.size(110.dp)
        )
        Text(text = canal.nombreCanal, color = Color.White, fontSize = 20.sp)
    }
}

