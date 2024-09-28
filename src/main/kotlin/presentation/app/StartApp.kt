package presentation.app

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.repository.ReceiptDBRepositoryImpl
import domain.usecase.ContinousPrintReceiptsUseCase
import domain.usecase.ContinousPullOfBIllAndReceiptsUseCase
import domain.usecase.GenerateReceiptUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.BackgroundTaskManager


fun main() = runBlocking {
    launch {
        launch { ContinousPullOfBIllAndReceiptsUseCase().execute() }
        launch { ContinousPrintReceiptsUseCase().execute() }
    }
    application(
        exitProcessOnExit = false
    ) {
        Window(
            title = "Kloud Sales Printer",
            onCloseRequest = {
                exitApplication()
            }) {
            App()
        }
    }
}