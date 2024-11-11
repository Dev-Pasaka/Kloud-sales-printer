import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import domain.usecase.ListenToEventsUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import presentation.app.App
import utils.KeyValueStorage


fun main() = runBlocking {
    application(
        exitProcessOnExit = true
    ) {

        launch {
            ListenToEventsUseCase().event()
        }

        Window(
            title = "Kloud Sales Printer",
            onCloseRequest = {
                exitApplication()
            }
        ) {
            App()
        }
    }

}

