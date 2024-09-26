package presentation.screens.auth.signInScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import presentation.screens.auth.signInScreen.components.SignInScreenComponent
import presentation.screens.dashboard.DashboardScreen

class SignInScreen():Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel = rememberScreenModel{SignInScreenViewModel()}
        val stationId by viewModel.stationId




        SignInScreenComponent(
            stationId = stationId,
            viewModel = viewModel,
            actionInitialize = {
                viewModel.initialize()
                navigator?.push(DashboardScreen())
            }
        )

    }
}