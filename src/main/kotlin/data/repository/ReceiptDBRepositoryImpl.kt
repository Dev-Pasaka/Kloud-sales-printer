package data.repository

import common.Resource
import data.local.database.Database
import data.local.entries.Receipt
import domain.model.PrintingLogs
import domain.model.PrintingStatus
import domain.repository.ReceiptDBRepository
import domain.usecase.GenerateReceiptUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class ReceiptDBRepositoryImpl(
    private val db: Database = Database,
) : ReceiptDBRepository {

    private val queries = db.database.appDatabaseQueries



    override suspend fun createReceipt(receiptObj: Receipt, receiptString:String,receiptId:String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                queries.insertReceipt(
                    receiptObj._id,
                    receiptObj.receiptNumber,
                    receiptObj.user,
                    receiptObj.date,
                    receiptObj.time,
                    receiptObj.data,
                    receiptObj.receiptName,
                    receiptObj.url,
                    receiptObj.status
                )
                ReceiptRepositoryImpl().generateImage(receiptString, receiptId)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


    override suspend fun delete(id: String): Boolean {
        return try {
            queries.deleteReceipt(id)
            true
        }catch (e:Exception){
            false
        }
    }


    override suspend fun getReceiptLogById(id: String): PrintingLogs? = withContext(Dispatchers.IO) {
       val result = queries.selectReceiptById(id).executeAsList().map { receipt ->
           PrintingLogs(
               id = receipt._id,
               receiptNumber = receipt.receiptNumber,
               user = receipt.user,
               date = receipt.date,
               time = receipt.time,
               data = receipt.data_,
               status = PrintingStatus.valueOf(receipt.status),
               url = receipt.url,
               fileName = receipt.receiptName
           )
       }.firstOrNull()

        return@withContext result

    }


    override suspend fun getAllReceipts(): List<PrintingLogs> {
        return withContext(Dispatchers.IO) {
            queries.selectAllReceipts().executeAsList().map { receipt ->
                PrintingLogs(
                    id = receipt._id,
                    receiptNumber = receipt.receiptNumber,
                    user = receipt.user,
                    date = receipt.date,
                    time = receipt.time,
                    data = receipt.data_,
                    status = PrintingStatus.valueOf(receipt.status),
                    url = receipt.url,
                    fileName = "${receipt._id}_receipt.png"
                )
            }
        }
    }

    override suspend fun updatePrintingStatus(receiptId: String, printingStatus: PrintingStatus): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val receipt = queries.selectReceiptById(receiptId).executeAsOneOrNull()
                receipt?.let {
                    queries.updateReceipt(
                        it.receiptNumber,
                        it.user,
                        it.date,
                        it.time,
                        it.data_,
                        it.receiptName,
                        it.url,
                        printingStatus.name,
                        it._id
                    )
                    true
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

}


suspend fun main(){
    val result = ReceiptDBRepositoryImpl().updatePrintingStatus(receiptId = "1", printingStatus = PrintingStatus.SUCCESS)
    println(result)

    ReceiptDBRepositoryImpl()
        .getAllReceipts().forEach {
            println(it.status)
        }
}