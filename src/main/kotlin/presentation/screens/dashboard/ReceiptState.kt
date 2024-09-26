package presentation.screens.dashboard

import domain.model.PrintingLogs

data class ReceiptState(
    val isLoaded: Boolean = false,
    val data: List<PrintingLogs> = emptyList(),
    val message: String = ""
)