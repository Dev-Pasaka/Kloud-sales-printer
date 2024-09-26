package presentation.screens.dashboard.components

import AppColors
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import presentation.screens.dashboard.DashboardScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SidePanel(
    onSelelectedScreen: (String) -> Unit
) {
    // Side panel content

    val navigator = LocalNavigator.current
    val list = listOf(
        Pair("drawables/logs_icon.png", "Logs"),
        Pair("drawables/settings.png", "Settings"),
    )
    Card(
        shape = RoundedCornerShape(
            topStart = CornerSize(0.dp),
            topEnd = CornerSize(8.dp),
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(8.dp)
        ),
        backgroundColor = AppColors.primaryColor.copy(alpha = 0.9f),
        elevation = 4.dp,
        modifier = Modifier.fillMaxHeight()
            .width(
                animateDpAsState(
                    targetValue = 130.dp,
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                ).value
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Icon(
                    painter = painterResource(resourcePath = "drawables/icons_logo.png"),
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                )
                Text(
                    text = "Kloud Sales",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(y = (-20).dp)
                )
            }
            LazyColumn {
                items(count = list.size){
                    val item = list[it]
                    Surface(
                        shape = CircleShape,
                        color = Color.Transparent,
                        onClick = {
                            when(item.second){
                                "Logs" -> {
                                    onSelelectedScreen(item.second)
                                }
                                "Settings" -> {
                                    onSelelectedScreen(item.second)
                                }
                            }
                        }

                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(resourcePath = item.first),
                                contentDescription = item.second,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = item.second,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }

            }
        }

    }
}
}