package investiment.message.banking.process.infra.adapters.outbound.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

/**
 * DTO for FMP Key Metrics API response.
 * Maps JSON response from FMP key metrics endpoint.
 */
data class FmpKeyMetricsResponse(
    @JsonProperty("marketCap")
    val marketCap: BigDecimal? = null,

    @JsonProperty("peRatio")
    val peRatio: BigDecimal? = null,

    @JsonProperty("pbRatio")
    val pbRatio: BigDecimal? = null,

    @JsonProperty("roic")
    val roic: BigDecimal? = null,

    @JsonProperty("dividendYield")
    val dividendYield: BigDecimal? = null,

    @JsonProperty("debtToEquity")
    val debtToEquity: BigDecimal? = null,

    @JsonProperty("operatingMargin")
    val operatingMargin: BigDecimal? = null,

    @JsonProperty("currentRatio")
    val currentRatio: BigDecimal? = null
)