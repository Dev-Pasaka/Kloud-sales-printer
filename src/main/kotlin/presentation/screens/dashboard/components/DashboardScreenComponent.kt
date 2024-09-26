package presentation.screens.dashboard.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import domain.model.PrintingStatus
import presentation.screens.dashboard.ReceiptState
import presentation.screens.dashboard.ReprintState
import kotlin.reflect.KProperty0

@Composable
fun DashboardScreenComponent(
    selectedScreen: String,
    receiptState: ReceiptState,
    onSelectedScreen: (String) -> Unit,
    actionReprint: (String, PrintingStatus) -> Unit,
    actionViewReceipt: () -> Unit,
    viewReceiptState: Boolean,
    image: String,
    actionSelectImage: (String) -> Unit,
    actionRefresh : () -> Unit,
    actionPrintReceipt: (String) -> Unit,
    deleteReceipt: (String, String) -> Unit,
) {
    Surface(
        color = AppColors.backgroundColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ){
            SidePanel(onSelelectedScreen = onSelectedScreen)
            MainPanel(
                selectedScreen = selectedScreen,
                receiptState = receiptState,
                actionReprint = actionReprint,
                viewReceiptState = viewReceiptState,
                actionViewReceipt = actionViewReceipt,
                image = image,
                actionSelectImage = actionSelectImage,
                actionRefresh = actionRefresh,
                actionPrintReceipt = actionPrintReceipt,
                deleteReceipt = deleteReceipt
            )
        }
    }
}