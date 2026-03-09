package investiment.message.banking.process.infra.adapters.outbound

import investiment.message.banking.process.domain.model.StockFundamentals
import investiment.message.banking.process.domain.model.StockQuote
import investiment.message.banking.process.infra.adapters.outbound.dto.FmpKeyMetricsResponse
import investiment.message.banking.process.infra.adapters.outbound.dto.FmpQuoteResponse
import org.springframework.stereotype.Component

/**
 * Mapper for converting FMP DTOs to domain models.
 * This follows the adapter pattern, translating external API structures
 * to internal domain structures.
 */
@Component
class FmpMapper {

    /**
     * Maps FMP quote response to domain StockQuote model.
     */
    fun toStockQuote(response: FmpQuoteResponse): StockQuote {
        return StockQuote(
            symbol = response.symbol,
            price = response.price,
            previousClose = response.previousClose,
            volume = response.volume,
            currency = response.currency ?: "USD"
        )
    }

    /**
     * Maps FMP key metrics response to domain StockFundamentals model.
     */
    fun toFundamentals(response: FmpKeyMetricsResponse, symbol: String): StockFundamentals {
        return StockFundamentals(
            symbol = symbol,
            marketCap = response.marketCap,
            pe = response.peRatio,
            pb = response.pbRatio,
            dividendYield = response.dividendYield,
            roic = response.roic,
            debtToEquity = response.debtToEquity,
            operatingMargin = response.operatingMargin,
            currentRatio = response.currentRatio
        )
    }
}