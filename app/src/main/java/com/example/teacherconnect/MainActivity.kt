package com.example.teacherconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.teacherconnect.ui.theme.TeacherConnectTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.material3.Surface
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.teacherconnect.navegacion.Rutas
val LocalBackgroundGradient = compositionLocalOf { mutableStateOf(
    listOf(
        Color(0xFF0E0A0B),
        Color(0xFF495765)
    )
)}
val LocalTextColor = compositionLocalOf { mutableStateOf(Color.White) }
val LocalBackgroundColor = compositionLocalOf { mutableStateOf(Color.DarkGray) }
val LocalBorderColor = compositionLocalOf { mutableStateOf(Color.LightGray) }
val LocalIsDarkMode = compositionLocalOf { mutableStateOf(true) }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeacherConnectTheme {
                Surface(
                    modifier=Modifier.fillMaxSize(),
                ){
                    App()
                }
            }
        }
    }
}
@Composable
fun App(){
    Surface(modifier= Modifier
        .fillMaxSize(),) {
        Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Rutas()
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TeacherConnectTheme {

    }
}