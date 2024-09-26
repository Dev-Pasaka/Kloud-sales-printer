package data.local.entries

data class Receipt(
    val _id: String,
    val receiptNumber: String,
    val user: String,
    val date: String,
    val time: String,
    val data: String,
    val receiptName: String,
    val url: String,
    val status: String

)