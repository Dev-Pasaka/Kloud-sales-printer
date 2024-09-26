package presentation.screens.dashboard.routes.logsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogsHeader() {

    Surface(
        color = AppColors.primaryColor.copy(alpha = 0.9f),
        shape = RoundedCornerShape(8.dp)
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
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(
                        text = "No. ",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                }

                Text(
                    text = "Receipt Number",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = "User",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = "Date",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = "Time",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = "Status",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = "Action",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}