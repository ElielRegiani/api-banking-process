package investiment.message.banking.process.infra.adapters.outbound

import investiment.message.banking.process.infra.adapters.outbound.dto.FmpKeyMetricsResponse
import investiment.message.banking.process.infra.adapters.outbound.dto.FmpQuoteResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FmpMapperTest {

    private lateinit var mapper: FmpMapper

    @BeforeEach
    fun setUp() {
        mapper = FmpMapper()
    }

    @Test
    fun `should map FmpQuoteResponse to StockQuote`() {
        // Given
        val quoteResponse = FmpQuoteResponse(
            symbol = "AAPL",
            price = BigDecimal("150.00"),
            previousClose = BigDecimal("149.00"),
            volume = 1000000L,
            currency = "USD"
        )

        // When
        val result = mapper.toStockQuote(quoteResponse)

        // Then
        assertNotNull(result)
        assertEquals("AAPL", result.symbol)
        assertEquals(BigDecimal("150.00"), result.price)
        assertEquals(BigDecimal("149.00"), result.previousClose)
        assertEquals(1000000L, result.volume)
        assertEquals("USD", result.currency)
    }

    @Test
    fun `should map FmpKeyMetricsResponse to StockFundamentals`() {
        // Given
        val metricsResponse = FmpKeyMetricsResponse(
            marketCap = BigDecimal("2500000000000"),
            peRatio = BigDecimal("25.00"),
            pbRatio = BigDecimal("35.00"),
            dividendYield = BigDecimal("0.005"),
            roic = BigDecimal("0.15"),
            debtToEquity = BigDecimal("0.5"),
            operatingMargin = BigDecimal("0.25"),
            currentRatio = BigDecimal("1.5")
        )

        // When
        val result = mapper.toFundamentals(metricsResponse, "AAPL")

        // Then
        assertNotNull(result)
        assertEquals("AAPL", result.symbol)
        assertEquals(BigDecimal("25.00"), result.pe)
        assertEquals(BigDecimal("35.00"), result.pb)
        assertEquals(BigDecimal("2500000000000"), result.marketCap)
    }

    @Test
    fun `should handle null values in quote response`() {
        // Given
        val quoteResponse = FmpQuoteResponse(
            symbol = "MSFT",
            price = BigDecimal("300.00"),
            previousClose = null,
            volume = null
        )

        // When
        val result = mapper.toStockQuote(quoteResponse)

        // Then
        assertNotNull(result)
        assertEquals("MSFT", result.symbol)
        assertEquals(null, result.previousClose)
        assertEquals(null, result.volume)
    }
}
