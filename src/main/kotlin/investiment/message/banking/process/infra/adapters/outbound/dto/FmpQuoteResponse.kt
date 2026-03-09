package investiment.message.banking.process.infra.adapters.outbound.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

/**
 * DTO for FMP Quote API response.
 * Maps JSON response from FMP quote endpoint.
 */
data class FmpQuoteResponse(
    @JsonProperty("symbol")
    val symbol: String,

    @JsonProperty("price")
    val price: BigDecimal,

    @JsonProperty("previousClose")
    val previousClose: BigDecimal? = null,

    @JsonProperty("volume")
    val volume: Long? = null,

    @JsonProperty("currency")
    val currency: String? = "USD"
)
