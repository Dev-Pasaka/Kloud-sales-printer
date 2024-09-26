package presentation.screens.dashboard

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import presentation.screens.dashboard.components.DashboardScreenComponent

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { DashboardScreenViewModel() }
        val selectedScreen  = viewModel.selectedScreen
        DashboardScreenComponent(
            selectedScreen = selectedScreen,
            receiptState = viewModel.receiptState,
            onSelectedScreen = {
                viewModel.selectScreen(it)
            },
            actionReprint = {id, status ->
                viewModel.reprint(id, status)
            },
            viewReceiptState = viewModel.viewReceiptState,
            image = viewModel.image,
            actionViewReceipt = viewModel::openOrCloseViewReceipt,
            actionSelectImage = {viewModel.selectImage(it)},actionRefresh = {viewModel.refresh()},
            actionPrintReceipt = {viewModel.printReceipt(it)},
            deleteReceipt = {fileName, id ->
                viewModel.deleteReceipt(fileName, id)
            }
        )
    }

}
