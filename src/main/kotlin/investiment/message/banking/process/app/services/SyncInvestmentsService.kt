package investiment.message.banking.process.app.services

import investiment.message.banking.process.domain.model.InvestmentSyncEvent
import investiment.message.banking.process.domain.model.StockSymbol
import investiment.message.banking.process.domain.ports.inbound.SyncInvestmentsUseCase
import investiment.message.banking.process.domain.ports.outbound.EventPublisher
import investiment.message.banking.process.domain.ports.outbound.InvestmentDataProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Service implementing the SyncInvestmentsUseCase.
 * This is the core business logic orchestrating the flow:
 * 1. Fetch investment data from external provider
 * 2. Create domain events
 * 3. Publish events to message broker
 */
@Service
class SyncInvestmentsService(
    private val provider: InvestmentDataProvider,
    private val publisher: EventPublisher,
    @Value("\${investment.symbols}") private val symbolsList: List<String>,
    @Value("\${investment.kafka.topic}") private val kafkaTopic: String
) : SyncInvestmentsUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sync(): Int {
        logger.info("Starting investment data synchronization for {} symbols", symbolsList.size)

        val symbols = symbolsList.map { StockSymbol(it) }
        var processedCount = 0

        symbols.forEach { symbol ->
            try {
                logger.debug("Fetching data for symbol: {}", symbol.ticker)

                val quote = provider.fetchQuote(symbol)
                val fundamentals = provider.fetchFundamentals(symbol)

                val event = InvestmentSyncEvent.from(quote, fundamentals)
                publisher.publish(event, kafkaTopic)

                processedCount++
                logger.info("Successfully processed symbol: {} - Price: {}", symbol.ticker, quote.price)

            } catch (e: Exception) {
                logger.error("Error processing symbol: {} - {}", symbol.ticker, e.message, e)
                // Continue processing other symbols even if one fails
            }
        }

        logger.info("Investment sync completed. Processed: {}/{} symbols", processedCount, symbols.size)
        return processedCount
    }
}