package domain.repository

import common.Resource
import data.remote.event.Event
import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import data.remote.response.getZReport.GetZreportRes
import kotlinx.coroutines.flow.Flow

interface GetReceiptsRepository {
    suspend fun listenReceipts(): Flow<Resource<Event>>

}