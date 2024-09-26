package presentation.screens.dashboard.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import org.apache.fontbox.ttf.TTFSubsetter

@Composable
fun SettingsScreen() {
    Surface(
        color = AppColors.backgroundColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Settings")
        }


    }
}