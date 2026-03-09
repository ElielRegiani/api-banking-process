package investiment.message.banking.process.infra.adapters.outbound

import investiment.message.banking.process.infra.adapters.outbound.dto.FmpKeyMetricsResponse
import investiment.message.banking.process.infra.adapters.outbound.dto.FmpQuoteResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/**
 * Feign client for Financial Modeling Prep (FMP) API.
 * This interface abstracts HTTP calls to FMP endpoints.
 */
@FeignClient(
    name = "fmpClient",
    url = "\${fmp.url}"
)
interface FmpFeignClient {

    /**
     * Fetches current quote data for a stock symbol.
     */
    @GetMapping("/quote/{symbol}")
    fun getQuote(
        @PathVariable symbol: String,
        @RequestParam apikey: String
    ): List<FmpQuoteResponse>

    /**
     * Fetches key metrics and fundamentals for a stock symbol.
     */
    @GetMapping("/key-metrics/{symbol}")
    fun getKeyMetrics(
        @PathVariable symbol: String,
        @RequestParam apikey: String
    ): List<FmpKeyMetricsResponse>
}