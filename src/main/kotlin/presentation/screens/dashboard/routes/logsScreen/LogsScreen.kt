package presentation.screens.dashboard.routes.logsScreen

import AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.model.PrintingStatus
import presentation.screens.dashboard.ReceiptState
import presentation.screens.dashboard.ReprintState

@Composable
fun LogsScreen(
    receiptState: ReceiptState,
    actionReprint: (String, PrintingStatus) -> Unit,
    image: String,
    viewReceiptState: Boolean,
    actionSelectImage: (String) -> Unit,
    actionRefresh: () -> Unit,
    actionClose: () -> Unit,
    actionPrint: (String) -> Unit,
    deleteReceipt: (String, String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                IconButton(
                    onClick = actionRefresh,
                    modifier = Modifier.background(
                        color = AppColors.primaryColor.copy(alpha = 0.3f)
                    )

                ){
                   Icon(
                       Icons.Default.Refresh,
                       contentDescription = "Refresh",
                       modifier = Modifier.size(32.dp),
                       tint = AppColors.primaryColor
                       )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            LogsScreenBody(
                receipts = receiptState,
                actionReprint = actionReprint,
                actionClose = actionClose,
                viewReceiptState = viewReceiptState,
                actionSelectImage = actionSelectImage,
                image = image,
                actionPrint = actionPrint,
                deleteReceipt = deleteReceipt

            )
        }
    }
}