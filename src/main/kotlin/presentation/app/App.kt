package presentation.app

import androidx.compose.animation.core.TweenSpec
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.SlideTransition
import presentation.screens.onboarding.splashScreen.SplashScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navigator = LocalNavigator.current
        Navigator(
            screen = SplashScreen(navigator = navigator)
        ){ navigate ->
            FadeTransition(navigate, animationSpec = TweenSpec(durationMillis = 1000))
        }
    }
}