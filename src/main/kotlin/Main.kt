import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import domain.usecase.ContinousPrintReceiptsUseCase
import domain.usecase.ContinousPullOfBIllAndReceiptsUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import presentation.app.App


fun main() = runBlocking {
    application(
        exitProcessOnExit = true
    ) {
        // Launch background tasks within the Compose lifecycle
        launch {
            ContinousPullOfBIllAndReceiptsUseCase().execute()
        }
        launch {
            ContinousPrintReceiptsUseCase().execute()
        }
        Window(
            title = "Kloud Sales Printer",
            onCloseRequest = {
                exitApplication()
            }
        ) {
            App() // Your main app content
        }
    }

}

