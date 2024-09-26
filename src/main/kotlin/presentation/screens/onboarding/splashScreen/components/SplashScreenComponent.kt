package presentation.screens.onboarding.splashScreen.components

import AppColors
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import presentation.screens.auth.signInScreen.SignInScreen

@Composable
fun SplashScreenComponent() {
    val navigator = LocalNavigator.current
    LaunchedEffect(Unit) {
        delay(3000)
       navigator?.push(SignInScreen())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.backgroundColor
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(resourcePath  = "drawables/kloud_sales_logo.png"),
                contentDescription = "Logo",
            )
        }
    }
}