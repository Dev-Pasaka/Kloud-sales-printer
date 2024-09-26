package presentation.screens.dashboard.routes.logsScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import domain.model.PrintingStatus
import presentation.screens.dashboard.ReceiptState
import presentation.screens.dashboard.ReprintState

@Composable
fun LogsScreenBody(
    receipts: ReceiptState,
    actionReprint: (String, PrintingStatus) -> Unit,
    actionClose: () -> Unit,
    viewReceiptState: Boolean,
    image: String,
    actionSelectImage: (String) -> Unit,
    actionPrint: (String) -> Unit,
    deleteReceipt: (String, String) -> Unit,

) {
    if (viewReceiptState){
        ViewReceipt(
            actionClose = actionClose,
            image = image,
            actionPrint = actionPrint
        )
    }

    LazyColumn {
        item {
            LogsHeader()
        }
        items(receipts.data.size) {
            val item = receipts.data[it]
            val no = it + 1
            LogsBodyItem(
                no = no,
                item = item,
                image = image,
                itemColor = if (
                    no % 2 == 1
                ) Color.Transparent else AppColors.backgroundColor,
                actionReprint = actionReprint,
                actionSelectImage = actionSelectImage,
                actionOpenOrCloseViewReceipt = actionClose,
                deleteReceipt = deleteReceipt
            )
        }
    }
}


