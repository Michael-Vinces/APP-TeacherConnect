package com.example.teacherconnect.interfaces.horario


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
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
import com.example.teacherconnect.firebase.Actividades
import com.example.teacherconnect.interfaces.login.InputField
import com.example.teacherconnect.navegacion.Pantallas
import java.util.Calendar

@Composable
fun FondoGestion(
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
fun FormActividadesScreen(navController: NavController) {
    val todasLasHoras = listOf("07:00","08:00", "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00")
    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
    val horarioviewmodel = viewModel<HorarioViewModel>()
    val horarioId by horarioviewmodel.horarioId.observeAsState(initial = "")
    val actividades by horarioviewmodel.actividades.observeAsState(initial = listOf())
    val diaSeleccionado = remember { mutableStateOf(dias[0]) }
    val nombre = rememberSaveable { mutableStateOf("") }
    val curso = rememberSaveable { mutableStateOf("") }
    val horasOcupadas by horarioviewmodel.horasOcupadas.observeAsState(initial = listOf())
    val horasDisponibles = todasLasHoras.filter { it !in horasOcupadas }
    val horasSeleccionadas = remember { mutableStateListOf<String>() }
    val textColor = LocalTextColor.current
    val isDarkMode = LocalIsDarkMode.current
    val backgroundColor = LocalBackgroundColor.current
    val borderColor = LocalBorderColor.current

    FondoGestion {
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
                .alpha(if (isDarkMode.value) 1.0f else 0.3f)
        )
        Box(modifier=Modifier.fillMaxSize()){
            Image(
                painter = painterResource(id = if (isDarkMode.value) R.drawable.logo_blanco else R.drawable.logo_negro),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(170.dp)
                    .padding(top = 15.dp, end = 40.dp)
            )
        }

        Row (
            modifier=Modifier.padding(top=70.dp)
                .clickable { navController.navigate(Pantallas.Home_HorarioConexion.name) },
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
        LaunchedEffect(key1 = horarioId) {
            horarioviewmodel.obtenerHorarioId()
        }
        LaunchedEffect(key1 = horarioId) {
            horarioviewmodel.obtenerActividadesPorHorarioId(horarioId)
        }
        Column (
        ){
            Column (verticalArrangement = Arrangement.Center){
                Text(
                    "Modifica \ntu horario", color = textColor.value, fontSize = 45.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 115.dp, start = 40.dp)
                        .align(Alignment.Start)
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        InputField(
                            valueState = nombre,
                            labelId = "Nombre de la actividad",
                            labelColor = textColor.value,
                            keyboardType = KeyboardType.Text
                        )
                        InputField(
                            valueState = curso,
                            labelId = "Curso",
                            labelColor = textColor.value,
                            keyboardType = KeyboardType.Text
                        )
                        Text(
                            modifier = Modifier.padding(top = 20.dp),
                            text = "Seleccione el día:",
                            fontSize = 18.sp,
                            color = textColor.value
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            items(dias) { dia ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clickable {
                                            diaSeleccionado.value = dia
                                            horarioviewmodel.obtenerHorasOcupadasParaDia(
                                                horarioId,
                                                dia
                                            )
                                            horasSeleccionadas.clear()
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(if (diaSeleccionado.value == dia) backgroundColor.value else Color.Transparent)
                                            .border(2.dp, color = borderColor.value)
                                    )
                                    Text(text = dia, color = textColor.value, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        if (diaSeleccionado.value.isNotEmpty()) {
                            Text(
                                text = "Seleccione las horas:",
                                fontSize = 18.sp,
                                color = textColor.value
                            )
                            Text(
                                text = "Máximo 3 horas seguidas",
                                fontSize = 18.sp,
                                color = textColor.value
                            )
                            LazyRow(
                                Modifier.padding(top = 16.dp, end = 20.dp, start = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(horasDisponibles) { hour ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clickable {
                                                if (horasSeleccionadas.contains(hour)) {
                                                    horasSeleccionadas.remove(hour)
                                                } else {
                                                    if (horasSeleccionadas.size < 3 && areHoursConsecutive(
                                                            hour,
                                                            horasSeleccionadas
                                                        )
                                                        && horarioviewmodel.isHourSelectionValid(
                                                            hour,
                                                            horasSeleccionadas,
                                                            todasLasHoras
                                                        )
                                                    ) {
                                                        horasSeleccionadas.add(hour)
                                                    }
                                                }
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .background(if (horasSeleccionadas.contains(hour)) backgroundColor.value else Color.Transparent)
                                                .border(2.dp, color = borderColor.value)
                                        )
                                        Text(text = hour, color = textColor.value, modifier = Modifier.padding(8.dp))
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                val actividad = Actividades(
                                    horarioId = horarioId,
                                    dia = diaSeleccionado.value,
                                    horaEntrada = horasSeleccionadas.minOrNull() ?: "",
                                    horaSalida = horasSeleccionadas.maxOrNull()
                                        ?.let { sumarUnaHora(it) }
                                        ?: "",
                                    nombre = nombre.value,
                                    aula = curso.value
                                )
                                horarioviewmodel.crearActividad(
                                    actividad,
                                    onSuccess = {
                                        nombre.value = ""
                                        curso.value = ""
                                        horasSeleccionadas.clear()
                                        horarioviewmodel.obtenerHorarioId()
                                        horarioviewmodel.obtenerHorasOcupadasParaDia(
                                            horarioId,
                                            diaSeleccionado.value
                                        )
                                        horarioviewmodel.obtenerActividadesPorHorarioId(horarioId)
                                    },
                                    onFailure = { exception ->
                                    }
                                )
                            },
                            modifier = Modifier.padding(top = 16.dp),
                            enabled = nombre.value.isNotEmpty() && curso.value.isNotEmpty() && diaSeleccionado.value.isNotEmpty() && horasSeleccionadas.isNotEmpty()
                        ) {
                            Text(text = "Guardar Actividad")
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            "Lista de Actividades", color = textColor.value, fontSize = 45.sp,
                            lineHeight = 50.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 25.dp, start = 40.dp)
                                .align(Alignment.Start)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                }
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Actividad", fontWeight = FontWeight.Bold,  color = textColor.value, fontSize = 15.sp, modifier = Modifier.width(100.dp))
                        Text(text = "Día", fontWeight = FontWeight.Bold,  color = textColor.value, fontSize = 15.sp, modifier = Modifier.width(100.dp))
                        Text(text = "Duración", fontWeight = FontWeight.Bold,  color = textColor.value, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(35.dp))

                    }
                }
                items(actividades) { actividad ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, color=borderColor.value)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = actividad.nombre,  color = textColor.value, fontSize = 15.sp, modifier = Modifier.width(100.dp))
                        Text(text = actividad.dia,  color = textColor.value, fontSize = 15.sp, modifier = Modifier.width(100.dp))
                        Text(text = "${actividad.horaEntrada} - ${actividad.horaSalida}",  color = textColor.value, fontSize = 15.sp)
                        Image(
                            painter = painterResource(id = R.drawable.icon_eliminarhorario),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .clickable {
                                    actividad.id?.let { horarioviewmodel.eliminarActividad(it) }
                                    horarioviewmodel.obtenerActividadesPorHorarioId(horarioId)
                                    horarioviewmodel.obtenerHorasOcupadasParaDia(
                                        horarioId,
                                        diaSeleccionado.value
                                    )
                                }
                        )
                    }
                }
                item{
                    Spacer(modifier = Modifier.height(50.dp))

                }
            }
        }
    }
}
fun areHoursConsecutive(hour: String, selectedHours: List<String>): Boolean {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    val selectedTimes = selectedHours.map { format.parse(it)?.time ?: 0L }.sorted()
    val currentHourTime = format.parse(hour)?.time ?: 0L
    if (selectedTimes.isEmpty()) return true

    return currentHourTime == selectedTimes.first() - TimeUnit.HOURS.toMillis(1) ||
            currentHourTime == selectedTimes.last() + TimeUnit.HOURS.toMillis(1)
}
fun sumarUnaHora(hora: String): String {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = format.parse(hora) ?: return hora
    val calendar = Calendar.getInstance().apply {
        time = date
        add(Calendar.HOUR, 1)
    }
    return format.format(calendar.time)
}
