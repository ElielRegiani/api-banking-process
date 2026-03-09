package investiment.message.banking.process.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class StockQuote(
    val symbol: String,
    val price: BigDecimal,
    val previousClose: BigDecimal? = null,
    val volume: Long? = null,
    val currency: String = "USD",
    val timestamp: LocalDateTime = LocalDateTime.now()
)