package investiment.message.banking.process.domain.ports.outbound

import investiment.message.banking.process.domain.model.StockFundamentals
import investiment.message.banking.process.domain.model.StockQuote
import investiment.message.banking.process.domain.model.StockSymbol

/**
 * Port for fetching investment data from external APIs.
 * This is an outbound port (dependency) in hexagonal architecture.
 */
interface InvestmentDataProvider {

    /**
     * Fetches current stock quote for a given symbol.
     *
     * @param symbol The stock symbol
     * @return StockQuote with current price and market data
     * @throws investiment.message.banking.process.domain.exception.QuoteNotFoundException if quote not found
     * @throws investiment.message.banking.process.domain.exception.ExternalApiException if API call fails
     */
    fun fetchQuote(symbol: StockSymbol): StockQuote

    /**
     * Fetches fundamental data for a given symbol.
     *
     * @param symbol The stock symbol
     * @return StockFundamentals with financial metrics
     * @throws investiment.message.banking.process.domain.exception.FundamentalsNotFoundException if fundamentals not found
     * @throws investiment.message.banking.process.domain.exception.ExternalApiException if API call fails
     */
    fun fetchFundamentals(symbol: StockSymbol): StockFundamentals
}