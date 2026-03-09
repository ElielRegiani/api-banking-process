package investiment.message.banking.process.infra.adapters.outbound

import investiment.message.banking.process.domain.exception.ExternalApiException
import investiment.message.banking.process.domain.exception.FundamentalsNotFoundException
import investiment.message.banking.process.domain.exception.QuoteNotFoundException
import investiment.message.banking.process.domain.model.StockFundamentals
import investiment.message.banking.process.domain.model.StockQuote
import investiment.message.banking.process.domain.model.StockSymbol
import investiment.message.banking.process.domain.ports.outbound.InvestmentDataProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * FMP (Financial Modeling Prep) adapter implementing the InvestmentDataProvider port.
 * This is an outbound adapter in hexagonal architecture,
 * connecting the domain to the FMP REST API.
 */
@Component
class FmpInvestmentAdapter(
    private val client: FmpFeignClient,
    private val mapper: FmpMapper,
    @Value("\${fmp.api-key}") private val apiKey: String
) : InvestmentDataProvider {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetchQuote(symbol: StockSymbol): StockQuote {
        return try {
            logger.debug("Fetching quote from FMP for symbol: {}", symbol.ticker)

            val response = client.getQuote(symbol.ticker, apiKey)

            if (response.isEmpty()) {
                throw QuoteNotFoundException(symbol.ticker)
            }

            mapper.toStockQuote(response.first())

        } catch (e: QuoteNotFoundException) {
            throw e
        } catch (e: Exception) {
            logger.error("Error fetching quote from FMP for symbol: {} - {}", symbol.ticker, e.message, e)
            throw ExternalApiException("Failed to fetch quote for ${symbol.ticker}", e)
        }
    }

    override fun fetchFundamentals(symbol: StockSymbol): StockFundamentals {
        return try {
            logger.debug("Fetching fundamentals from FMP for symbol: {}", symbol.ticker)

            val response = client.getKeyMetrics(symbol.ticker, apiKey)

            if (response.isEmpty()) {
                throw FundamentalsNotFoundException(symbol.ticker)
            }

            mapper.toFundamentals(response.first(), symbol.ticker)

        } catch (e: FundamentalsNotFoundException) {
            throw e
        } catch (e: Exception) {
            logger.error("Error fetching fundamentals from FMP for symbol: {} - {}", symbol.ticker, e.message, e)
            throw ExternalApiException("Failed to fetch fundamentals for ${symbol.ticker}", e)
        }
    }
}