package presentation.screens.dashboard.routes.logsScreen

import AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.PrintingLogs
import domain.model.PrintingStatus
import presentation.screens.dashboard.ReprintState

@Composable
fun LogsBodyItem(
    no: Int = 1,
    item: PrintingLogs,
    image: String,
    itemColor: Color = Color.White,
    actionReprint: (String, PrintingStatus) -> Unit,
    actionSelectImage: (String) -> Unit,
    actionOpenOrCloseViewReceipt: () -> Unit,
    deleteReceipt: (String, String) -> Unit,

    ) {

    Surface(
        color = itemColor,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "$no",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                }

                Text(
                    text = item.receiptNumber,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = item.user,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = item.date,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = item.time,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = item.status?.name ?: "",
                    color = when (item.status) {
                        PrintingStatus.PRINTING -> Color.Yellow
                        PrintingStatus.SUCCESS -> Color.Green
                        PrintingStatus.FALIED -> Color.Red
                        PrintingStatus.PENDING -> Color.Yellow

                        null -> TODO()
                    },
                    fontSize = 14.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            deleteReceipt(item.fileName, item.id)
                            println("Image name ${item.fileName}")
                        },
                        modifier = Modifier.background(
                            color = Color.Red.copy(alpha = 0.2f),
                            shape = CircleShape
                        )

                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.padding(4.dp)

                        )
                    }

                    TextButton(
                        onClick = {
                            actionSelectImage(item.fileName)
                            actionOpenOrCloseViewReceipt()
                        },
                        shape = CircleShape,
                    ) {
                        Text(
                            text = "View",
                            textDecoration = TextDecoration.Underline,
                            color = AppColors.primaryColor,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }

}