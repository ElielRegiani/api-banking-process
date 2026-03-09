package investiment.message.banking.process.infra.adapters.inbound

import investiment.message.banking.process.domain.ports.inbound.SyncInvestmentsUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for investment synchronization endpoints.
 * This is an inbound adapter in hexagonal architecture,
 * exposing the use case through HTTP API.
 */
@RestController
@RequestMapping("/api/v1/investments")
class InvestmentController(
    private val syncUseCase: SyncInvestmentsUseCase
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Triggers manual synchronization of investment data.
     *
     * @return Response with number of processed symbols
     */
    @PostMapping("/sync")
    fun triggerSync(): ResponseEntity<SyncResponse> {
        logger.info("Manual sync request received")

        return try {
            val processedCount = syncUseCase.sync()
            ResponseEntity.ok(
                SyncResponse(
                    success = true,
                    message = "Synchronization completed successfully",
                    processedSymbols = processedCount
                )
            )
        } catch (e: Exception) {
            logger.error("Manual sync failed: {}", e.message, e)
            ResponseEntity.status(500).body(
                SyncResponse(
                    success = false,
                    message = "Synchronization failed: ${e.message}",
                    processedSymbols = 0
                )
            )
        }
    }
}

/**
 * Response DTO for sync endpoint.
 */
data class SyncResponse(
    val success: Boolean,
    val message: String,
    val processedSymbols: Int
)
