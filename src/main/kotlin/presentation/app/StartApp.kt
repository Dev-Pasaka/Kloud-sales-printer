package presentation.app

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.repository.ReceiptDBRepositoryImpl
import domain.usecase.GenerateReceiptUseCase
import kotlinx.coroutines.launch


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}