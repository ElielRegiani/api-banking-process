package investiment.message.banking.process.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain event representing a synchronized investment data point.
 * This is published to external systems via EventPublisher.
 */
data class InvestmentSyncEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val symbol: String,
    val price: BigDecimal,
    val previousClose: BigDecimal? = null,
    val volume: Long? = null,
    val currency: String = "USD",
    val marketCap: BigDecimal? = null,
    val pe: BigDecimal? = null,
    val pb: BigDecimal? = null,
    val dividendYield: BigDecimal? = null,
    val roic: BigDecimal? = null,
    val debtToEquity: BigDecimal? = null,
    val operatingMargin: BigDecimal? = null,
    val currentRatio: BigDecimal? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val provider: String = "FMP"
) {

    companion object {
        fun from(quote: StockQuote, fundamentals: StockFundamentals): InvestmentSyncEvent {
            return InvestmentSyncEvent(
                symbol = quote.symbol,
                price = quote.price,
                previousClose = quote.previousClose,
                volume = quote.volume,
                currency = quote.currency,
                marketCap = fundamentals.marketCap,
                pe = fundamentals.pe,
                pb = fundamentals.pb,
                dividendYield = fundamentals.dividendYield,
                roic = fundamentals.roic,
                debtToEquity = fundamentals.debtToEquity,
                operatingMargin = fundamentals.operatingMargin,
                currentRatio = fundamentals.currentRatio,
                timestamp = quote.timestamp
            )
        }
    }
}
