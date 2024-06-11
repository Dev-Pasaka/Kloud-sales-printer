package domain.usecase

import data.repository.ReceiptRepositoryImpl
import domain.repository.ReceiptRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GenerateReceiptUseCase(
    private val receiptRepository: ReceiptRepository = ReceiptRepositoryImpl()
) {
    suspend fun generateReceipt(
        receiptContent:String,
        qrData:String,
        size:Int = 150,
        fileName:String,
    ):Boolean = withContext(Dispatchers.IO){
        receiptRepository.generateReceiptWithQR(
            receiptContent = receiptContent,
            qrLink = qrData,
            filePath = receiptRepository.generateFileName(fileName)
        )
    }
}


suspend fun main(){
    val formattedContent = """
        {Ubuniworks Ltd}:
        P.O BOX 16779 00100
        Nairobi, Kenya
        
        {Receipt}:
        Order Type : Order Out
        Receipt No : 686
        Customer/Table : none
        Waiter : Shadrack Kristian
        Station : Test Station
        
        {Product(s)}:
        1 Honey (KES 1)  50 pcs  = KES 50
        Total : KES 50.0
        
        Amount Paid : KES 50
        Balance : KES 0
        Payment Method : Cash
        
        {Etims Information}:
        SCU ID : Pending
        Invoice No: Pending
        Internal Data : Pending
        Receipt Signature : Pending
        
        Thank you
        Printed on : Fri, 7 Jun 2024 :21:00:52
        Powered by Ubuniworks
        KloudSales - POS
        Email : info@ubuniworks.com
        Phone : +254717722324
        """.trimIndent()
    val result = GenerateReceiptUseCase().generateReceipt(
        receiptContent = formattedContent,
        qrData = "https://coinx.co.ke/",
        fileName = "text1.png"
    )
    println(result)
}