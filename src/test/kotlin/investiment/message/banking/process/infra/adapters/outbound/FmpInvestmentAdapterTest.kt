package investiment.message.banking.process.infra.adapters.outbound

import investiment.message.banking.process.domain.exception.ExternalApiException
import investiment.message.banking.process.domain.exception.QuoteNotFoundException
import investiment.message.banking.process.domain.model.StockSymbol
import investiment.message.banking.process.infra.adapters.outbound.dto.FmpKeyMetricsResponse
import investiment.message.banking.process.infra.adapters.outbound.dto.FmpQuoteResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FmpInvestmentAdapterTest {

    private lateinit var fmpFeignClient: FmpFeignClient
    private lateinit var fmpMapper: FmpMapper
    private lateinit var adapter: FmpInvestmentAdapter

    @BeforeEach
    fun setUp() {
        fmpFeignClient = mock()
        fmpMapper = FmpMapper()
        adapter = FmpInvestmentAdapter(
            client = fmpFeignClient,
            mapper = fmpMapper,
            apiKey = "test-api-key"
        )
    }

    @Test
    fun `should fetch quote successfully`() {
        // Given
        val symbol = StockSymbol("AAPL")
        val quoteResponse = FmpQuoteResponse(
            symbol = "AAPL",
            price = BigDecimal("150.00"),
            previousClose = BigDecimal("149.00"),
            volume = 1000000L
        )
        `when`(fmpFeignClient.getQuote("AAPL", "test-api-key")).thenReturn(listOf(quoteResponse))

        // When
        val result = adapter.fetchQuote(symbol)

        // Then
        assertNotNull(result)
        assertEquals("AAPL", result.symbol)
        assertEquals(BigDecimal("150.00"), result.price)
    }

    @Test
    fun `should throw QuoteNotFoundException when quote not found`() {
        // Given
        val symbol = StockSymbol("INVALID")
        `when`(fmpFeignClient.getQuote("INVALID", "test-api-key")).thenReturn(emptyList())

        // When & Then
        assertThrows<QuoteNotFoundException> {
            adapter.fetchQuote(symbol)
        }
    }

    @Test
    fun `should throw ExternalApiException when API call fails`() {
        // Given
        val symbol = StockSymbol("AAPL")
        `when`(fmpFeignClient.getQuote("AAPL", "test-api-key"))
            .thenThrow(RuntimeException("API Error"))

        // When & Then
        assertThrows<ExternalApiException> {
            adapter.fetchQuote(symbol)
        }
    }

    @Test
    fun `should fetch fundamentals successfully`() {
        // Given
        val symbol = StockSymbol("AAPL")
        val fundamentalsResponse = FmpKeyMetricsResponse(
            marketCap = BigDecimal("2500000000000"),
            peRatio = BigDecimal("25.00"),
            pbRatio = BigDecimal("35.00"),
            dividendYield = BigDecimal("0.005")
        )
        `when`(fmpFeignClient.getKeyMetrics("AAPL", "test-api-key"))
            .thenReturn(listOf(fundamentalsResponse))

        // When
        val result = adapter.fetchFundamentals(symbol)

        // Then
        assertNotNull(result)
        assertEquals("AAPL", result.symbol)
        assertEquals(BigDecimal("25.00"), result.pe)
    }
}
