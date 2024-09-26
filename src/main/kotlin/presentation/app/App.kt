package presentation.app

import androidx.compose.animation.core.TweenSpec
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import data.dto.response.ReceiptRes
import data.local.entries.Receipt
import data.repository.ReceiptDBRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.model.PrintingStatus
import domain.repository.jsonString
import domain.usecase.GenerateReceiptUseCase
import domain.usecase.PrintPendingReceiptsUseCase
import domain.usecase.PrintReceiptUseCase
import domain.usecase.PullNewReceiptsUseCase
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

import presentation.screens.onboarding.splashScreen.SplashScreen
import java.util.Date
import java.util.UUID

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()

    MaterialTheme {
        val navigator = LocalNavigator.current
        Navigator(
            screen = SplashScreen(navigator = navigator)
        ){ navigate ->
            SlideTransition(navigate, animationSpec = TweenSpec(durationMillis = 1000))
        }
    }
}