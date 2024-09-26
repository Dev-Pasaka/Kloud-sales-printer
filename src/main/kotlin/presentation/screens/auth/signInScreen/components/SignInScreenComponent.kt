package presentation.screens.auth.signInScreen.components

import AppColors
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.screens.auth.signInScreen.SignInScreenViewModel

@Composable
fun SignInScreenComponent(
    stationId:String,
    viewModel: SignInScreenViewModel,
    actionInitialize:()->Unit
) {
    Surface(
        color = AppColors.backgroundColor,
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            SignInScreenUpperSection()
            SignInScreenBodySection(
                actionInitialize = actionInitialize,
                businessId = stationId,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}