package presentation.screens.onboarding.splashScreen

import SplashScreenViewModel
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import presentation.screens.onboarding.splashScreen.components.SplashScreenComponent

class SplashScreen(val navigator: Navigator?):Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { SplashScreenViewModel() }
        SplashScreenComponent()
    }
}