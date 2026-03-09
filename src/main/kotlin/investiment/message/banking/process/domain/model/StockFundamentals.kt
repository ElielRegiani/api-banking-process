package investiment.message.banking.process.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class StockFundamentals(
    val symbol: String,
    val marketCap: BigDecimal? = null,
    val pe: BigDecimal? = null,
    val pb: BigDecimal? = null,
    val dividendYield: BigDecimal? = null,
    val roic: BigDecimal? = null,
    val debtToEquity: BigDecimal? = null,
    val operatingMargin: BigDecimal? = null,
    val currentRatio: BigDecimal? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
