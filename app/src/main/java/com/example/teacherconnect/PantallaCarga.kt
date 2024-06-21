package com.example.teacherconnect

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.teacherconnect.navegacion.Pantallas
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun PantallaCarga(navController: NavController){
    GradientBackground {
        val scale=remember{
            Animatable(0f)
        }
        LaunchedEffect(key1=true){
            scale.animateTo(targetValue=0.9f,
                animationSpec = tween(durationMillis = 1000,
                    easing = {
                        OvershootInterpolator(8f)
                            .getInterpolation(it)
                    }
                ),

                )
            delay(2000L)
            navController.navigate(Pantallas.LoginConexion.name)
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_blanco),
                contentDescription = null,
                modifier = Modifier
                    .scale(scale.value)
            )
        }


    }

}
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF264D80),
                        Color(0xFFCC26BC)
                    )
                )
            )
    ) {
        content()
    }
}