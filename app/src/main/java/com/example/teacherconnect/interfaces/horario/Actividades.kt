package com.example.teacherconnect.interfaces.horario


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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.teacherconnect.LocalBackgroundColor
import com.example.teacherconnect.LocalBackgroundGradient
import com.example.teacherconnect.LocalBorderColor
import com.example.teacherconnect.LocalIsDarkMode
import com.example.teacherconnect.LocalTextColor
import com.example.teacherconnect.R
import com.example.teacherconnect.navegacion.Pantallas

@Composable
fun FondoActividades(
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
fun ActividadesScreen(navController: NavController) {
    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    val diaSeleccionado = remember { mutableStateOf("Lunes") }
    val horarioviewmodel = viewModel<HorarioViewModel>()
    val horarioId by horarioviewmodel.horarioId.observeAsState(initial = "")
    val actividades by horarioviewmodel.actividades.observeAsState(initial = listOf())
    val textColor = LocalTextColor.current
    val isDarkMode = LocalIsDarkMode.current
    val backgroundColor = LocalBackgroundColor.current
    val borderColor = LocalBorderColor.current

    FondoActividades {
        LaunchedEffect(key1 = horarioId) {
            horarioviewmodel.obtenerHorarioId()
        }
        LaunchedEffect(key1 = horarioId) {
            horarioviewmodel.obtenerActividadesPorHorarioIdYDia(horarioId, diaSeleccionado.value)
        }

        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 30.dp)
            ) {
                items(dias) { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier
                            .width(150.dp)
                            .height(35.dp)
                            .padding(bottom = 4.dp)
                            .background(
                                if (diaSeleccionado.value == dia) Color(0xFF1FE7D2) else Color.White,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                            .clickable {
                                diaSeleccionado.value = dia
                                horarioviewmodel.obtenerActividadesPorHorarioIdYDia(horarioId, dia)
                            },
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(230.dp))
                Text(
                    text = diaSeleccionado.value,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = if(!isDarkMode.value) Color.White else Color(0xFF1FE7D2)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                        .width(330.dp)
                        .height(380.dp)
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    if (actividades.isEmpty()) {
                        Text(
                            text = "No hay actividades asignadas a este día",
                            color = Color.DarkGray,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn {
                            items(actividades) { actividad ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${actividad.horaEntrada}\n${actividad.horaSalida}",
                                        color = Color(0xFF16CCB9),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth(0.7f)
                                    ) {
                                        Text(
                                            text = actividad.nombre,
                                            color = Color.DarkGray,
                                            fontSize = 21.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(150.dp)
                                        )
                                        Text(
                                            text = actividad.aula,
                                            color = Color.DarkGray,
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(150.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(1.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .background(Color.Black)
                                )
                                Spacer(modifier = Modifier.height(1.dp))                            }
                        }
                    }
                }
            }
            Box (modifier = Modifier.fillMaxSize()){
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 50.dp)
                        .clickable { navController.navigate(Pantallas.Home_HorarioConexion.name) },
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
                            color = textColor.value,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(start = 3.dp)
                    )
                }
            }
        }
    }
}