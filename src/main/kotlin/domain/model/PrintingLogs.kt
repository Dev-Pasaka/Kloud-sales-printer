package domain.model


data class PrintingLogs(
    val id:String = "",
    val fileName:String,
    val url:String,
    val receiptNumber: String,
    val user: String,
    val date: String,
    val data: String,
    val time: String,
    val status: PrintingStatus?,
)

enum class PrintingStatus {
    PRINTING,
    SUCCESS,
    FALIED,
    PENDING,
}
