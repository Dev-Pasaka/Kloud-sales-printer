package domain.repository

import common.Resource
import data.local.entries.Receipt
import domain.model.PrintingLogs
import domain.model.PrintingStatus
import kotlinx.coroutines.flow.Flow

interface ReceiptDBRepository {
    suspend fun createReceipt(printingStatusObj: Receipt, receiptString:String,receiptId:String): Boolean
    suspend fun delete(id:String):Boolean
    suspend fun getReceiptLogById(id: String): PrintingLogs?
    suspend fun getAllReceipts(): List<PrintingLogs>
    suspend fun updatePrintingStatus(receiptId:String, printingStatus: PrintingStatus): Boolean
    }
