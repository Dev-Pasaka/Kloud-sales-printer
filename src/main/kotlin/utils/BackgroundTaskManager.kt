package utils


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


object BackgroundTaskManager {
    // Scheduled executor for background task execution
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    suspend fun execute() = withContext(Dispatchers.Main) {
        scheduler.scheduleAtFixedRate(
            {
                launch {
                    println("Pulling and printing receipts")
                   // doBackgroundWork()
                }
            },
            0, 1, TimeUnit.SECONDS  // 0 initial delay, repeat every 10 seconds
        )
    }


    // Stop the scheduler
    fun stopScheduler() {
        scheduler.shutdown()
        println("Scheduler stopped")
    }
}

