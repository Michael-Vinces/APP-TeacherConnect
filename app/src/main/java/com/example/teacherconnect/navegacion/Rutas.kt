package com.example.teacherconnect.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teacherconnect.PantallaCarga
import com.example.teacherconnect.interfaces.canal.ChannelScreen
import com.example.teacherconnect.interfaces.canal.ChatScreen
import com.example.teacherconnect.interfaces.canal.Home_CanalScreen
import com.example.teacherconnect.interfaces.home.ConfiguracionScreen
import com.example.teacherconnect.interfaces.home.Home
import com.example.teacherconnect.interfaces.home.Notificaciones
import com.example.teacherconnect.interfaces.horario.ActividadesScreen
import com.example.teacherconnect.interfaces.horario.FormActividadesScreen
import com.example.teacherconnect.interfaces.horario.Home_HorariosScreen
import com.example.teacherconnect.interfaces.login.LoginScreen

@Composable
fun Rutas(){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = Pantallas.PantallaCargaConexion.name){
        composable(Pantallas.PantallaCargaConexion.name){
            PantallaCarga(navController=navController)
        }
        composable(Pantallas.LoginConexion.name){
            LoginScreen(navController=navController)
        }
        composable(Pantallas.HomeConexion.name){
            Home(navController=navController)
        }
        composable(Pantallas.Home_HorarioConexion.name){
            Home_HorariosScreen(navController=navController)
        }
        composable(Pantallas.FormActividadesConexion.name){
            FormActividadesScreen(navController=navController)
        }
        composable(Pantallas.ActividadesConexion.name){
            ActividadesScreen(navController=navController)
        }
        composable(Pantallas.Home_CanalConexion.name){
            Home_CanalScreen(navController=navController)
        }
        composable(Pantallas.TusCanalesConexion.name){
            ChannelScreen(navController=navController)
        }
        composable(Pantallas.ConfiguracionConexion.name){
            ConfiguracionScreen(navController=navController)
        }
        composable("ChatConexion/canalId={canalId}") { backStackEntry ->
            val canalId = backStackEntry.arguments?.getString("canalId")
            ChatScreen(navController= navController,canalId)
        }
        composable(Pantallas.NotificacionesConexion.name){
            Notificaciones(navController=navController)
        }
    }
}