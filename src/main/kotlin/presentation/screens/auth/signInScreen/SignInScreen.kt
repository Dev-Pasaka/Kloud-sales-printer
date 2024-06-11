package presentation.screens.auth.signInScreen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

class SignInScreen(val navigator: Navigator):Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel{SignInScreenViewModel()}

    }
}