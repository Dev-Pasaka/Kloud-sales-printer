package domain.usecase

import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

class ContinousPullOfBIllAndReceiptsUseCase(
    private val pullNewBillsUseCase: PullNewBillsUseCase= PullNewBillsUseCase(),
    private val pullNewReceiptsUseCase: PullNewReceiptsUseCase = PullNewReceiptsUseCase()
) {
    suspend fun execute() = withContext(Dispatchers.IO) {
        while (true){
            try {
                launch { pullNewBillsUseCase.pullBills() }
                launch { pullNewReceiptsUseCase.pullReceipts() }
            }catch (e: Exception){
                e.printStackTrace()
                if (e == CancellationException()) throw e else null
            }
            delay(2000)
        }

    }

}