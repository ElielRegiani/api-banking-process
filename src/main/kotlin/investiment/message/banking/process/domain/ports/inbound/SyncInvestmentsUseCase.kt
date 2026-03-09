package investiment.message.banking.process.domain.ports.inbound

/**
 * Inbound port (use case) for synchronizing investment data.
 * This is an inbound port in hexagonal architecture that can be triggered
 * by external actors like HTTP requests, scheduled tasks, or event listeners.
 */
interface SyncInvestmentsUseCase {

    /**
     * Synchronizes investment data from external provider to event publisher.
     * Fetches quotes and fundamentals for all configured symbols and publishes events.
     *
     * @return Number of successfully processed symbols
     * @throws investiment.message.banking.process.domain.exception.InvestmentException if sync fails
     */
    fun sync(): Int
}