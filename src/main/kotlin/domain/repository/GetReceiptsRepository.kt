package domain.repository

import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import data.remote.response.getZReport.GetZreportRes

interface GetReceiptsRepository {
    suspend fun getReceipts(body:GetReceiptsReq):List<GetReceiptsResItem>
    suspend fun getBills(body:GetReceiptsReq):List<GetReceiptsResItem>
    suspend fun updatePrintedReceiptOrBill(id:String):Boolean
    suspend fun getZReport():GetZreportRes?

}