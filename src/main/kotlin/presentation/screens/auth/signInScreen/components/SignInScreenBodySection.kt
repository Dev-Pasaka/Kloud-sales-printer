package presentation.screens.auth.signInScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import presentation.screens.auth.signInScreen.SignInScreenViewModel
import kotlin.math.truncate

@Composable
fun SignInScreenBodySection(
    actionInitialize: () -> Unit,
    businessId: String,
    viewModel: SignInScreenViewModel,
) {

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            label = {
                Text(
                    text = "Station Id",
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            singleLine = true,
            value = businessId,
            onValueChange = {viewModel.businessId(it)},
            modifier = Modifier.padding(bottom = 16.dp)
                .width(400.dp),

        )

        Button(
            onClick = actionInitialize,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColors.primaryColor,
                contentColor = Color.White
            )
        ){
            Text(text = "Initialize")
        }

    }
}