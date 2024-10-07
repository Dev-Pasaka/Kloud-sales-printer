package domain.usecase

import data.repository.GetReceiptsRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.repository.GetReceiptsRepository
import domain.repository.ReceiptRepository
import kotlinx.coroutines.delay

class ContinousPullOfZReportsUseCase(
    private val receiptsRepository: ReceiptRepository = ReceiptRepositoryImpl(),
    private val getReceiptsRepository: GetReceiptsRepository = GetReceiptsRepositoryImpl()
) {
   suspend operator fun invoke(){
      while (true){
          try {
              val results = getReceiptsRepository.getZReport()
              val html = receiptsRepository.convertJsonToFormattedZReportString(results!!)
              ReceiptRepositoryImpl().generateZReportImage(html)
              delay(3000)
          }catch (e:Exception){
              e.printStackTrace()
          }
      }
   }
}