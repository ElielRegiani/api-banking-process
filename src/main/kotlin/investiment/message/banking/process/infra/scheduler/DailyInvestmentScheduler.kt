package investiment.message.banking.process.infra.scheduler

import investiment.message.banking.process.domain.ports.inbound.SyncInvestmentsUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Scheduler for triggering periodic investment data synchronization.
 * This is an inbound adapter in hexagonal architecture,
 * triggering the use case from an external event (scheduled task).
 */
@Component
@EnableScheduling
class DailyInvestmentScheduler(
    private val useCase: SyncInvestmentsUseCase
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Runs daily at 8 AM to synchronize investment data.
     * Cron expression: 0 0 8 * * * (8:00 AM every day)
     */
    @Scheduled(cron = "0 0 8 * * *")
    fun runDailySync() {
        logger.info("Starting scheduled investment synchronization at {}", LocalDateTime.now())

        try {
            val processedCount = useCase.sync()
            logger.info("Scheduled sync completed successfully. Processed {} symbols", processedCount)
        } catch (e: Exception) {
            logger.error("Scheduled sync failed: {}", e.message, e)
            // Don't rethrow to avoid stopping the scheduler
        }
    }
}