package presentation.screens.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import common.Resource
import domain.model.PrintingStatus
import domain.usecase.*
import kotlinx.coroutines.launch

class DashboardScreenViewModel(
    private val getReceiptUseCase: GetAllReceipts = GetAllReceipts(),
    private val reprintUseCase: ReprintUseCase = ReprintUseCase(),
    private val printReceiptUseCase: PrintReceiptUseCase = PrintReceiptUseCase(),
    private val deleteReceiptUseCase: DeleteReceiptUseCase = DeleteReceiptUseCase(),
): ScreenModel {
    var selectedScreen by mutableStateOf("Logs")
        private set

    fun selectScreen(screen: String) {
        selectedScreen = screen
    }

    var receiptState by mutableStateOf(ReceiptState())
        private set

    var reprintState by mutableStateOf(ReprintState())
        private set

    var viewReceiptState by mutableStateOf(false)
        private set

    var image by mutableStateOf("")
        private set

    fun openOrCloseViewReceipt(){ viewReceiptState = !viewReceiptState }
    fun selectImage(imageLink: String){
        println("ImageLink: $imageLink")
        image = imageLink
    }

    private fun getReceipts() {
        screenModelScope.launch {
            getReceiptUseCase.getAllReceipts().collect{ status ->
                receiptState = when(status){
                    is Resource.Success -> {
                        ReceiptState(isLoaded = true, data = status.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        ReceiptState(isLoaded = false, message = status.message ?: "")
                    }
                    is Resource.Loading -> {
                       ReceiptState(isLoaded = true,message = status.message ?: "")
                    }

                }
            }
        }
    }


    fun refresh(){
        getReceipts()
    }

    fun reprint(receiptId:String, printingStatus: PrintingStatus){
        screenModelScope.launch {
            reprintState = reprintState.copy(isLoading = true)
            reprintUseCase.reprint(receiptId, printingStatus)
            getReceipts()
            reprintState = reprintState.copy(isLoading = false)

        }
    }

    fun printReceipt(receiptName:String){
        screenModelScope.launch {
            printReceiptUseCase.printReceipt("./receipts/$receiptName")
        }
    }

    fun deleteReceipt(fileName:String, id:String){
        screenModelScope.launch {
            deleteReceiptUseCase.deleteReceipt(fileName, id)
            getReceipts()
        }
    }






    init {
        screenModelScope.launch {
            PullNewReceiptsUseCase().pullReceipts().also {
                PrintPendingReceiptsUseCase().printPendingReceipts()
            }
        }
        screenModelScope.launch {
            PullNewBillsUseCase().pullBills().also {
                PrintPendingReceiptsUseCase().printPendingReceipts()
            }
        }
        getReceipts()

    }


}