package presentation.screens.dashboard.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import domain.model.PrintingStatus
import presentation.screens.dashboard.ReceiptState
import presentation.screens.dashboard.ReprintState
import presentation.screens.dashboard.routes.logsScreen.LogsScreen
import presentation.screens.dashboard.routes.SettingsScreen

@Composable
fun MainPanel(
    selectedScreen: String,
    receiptState: ReceiptState,
    actionReprint: (String, PrintingStatus) -> Unit,
    actionViewReceipt: () -> Unit,
    image: String,
    actionSelectImage: (String) -> Unit,
    viewReceiptState: Boolean,
    actionRefresh : () -> Unit,
    actionPrintReceipt: (String) -> Unit,
    deleteReceipt: (String, String) -> Unit,

) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) {
        when (selectedScreen) {
            "Logs" -> LogsScreen(
                receiptState = receiptState,
                actionReprint = actionReprint,
                actionClose = actionViewReceipt,
                image = image,
                actionSelectImage = actionSelectImage,
                viewReceiptState = viewReceiptState,
                actionRefresh = actionRefresh,
                actionPrint = actionPrintReceipt,
                deleteReceipt = deleteReceipt
            )

            "Settings" -> SettingsScreen()
        }

    }
}


