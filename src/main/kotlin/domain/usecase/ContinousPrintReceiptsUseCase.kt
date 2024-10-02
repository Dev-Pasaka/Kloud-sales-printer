package domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class ContinousPrintReceiptsUseCase(
    private val printPendingReceiptsUseCase: PrintPendingReceiptsUseCase = PrintPendingReceiptsUseCase()
) {
    suspend fun execute() = withContext(Dispatchers.IO) {
        while (true){
            try {
                launch { printPendingReceiptsUseCase.printPendingReceipts() }
            }catch (e: Exception){
                e.printStackTrace()
                if (e == CancellationException()) throw e else null
            }
            delay(1000)
        }

    }
}