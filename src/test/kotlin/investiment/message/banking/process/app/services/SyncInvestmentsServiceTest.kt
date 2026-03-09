package investiment.message.banking.process.app.services

import investiment.message.banking.process.domain.model.InvestmentSyncEvent
import investiment.message.banking.process.domain.model.StockFundamentals
import investiment.message.banking.process.domain.model.StockQuote
import investiment.message.banking.process.domain.model.StockSymbol
import investiment.message.banking.process.domain.ports.outbound.EventPublisher
import investiment.message.banking.process.domain.ports.outbound.InvestmentDataProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import kotlin.test.assertEquals

class SyncInvestmentsServiceTest {

    private lateinit var investmentDataProvider: InvestmentDataProvider
    private lateinit var eventPublisher: EventPublisher
    private lateinit var service: SyncInvestmentsService

    @Captor
    private lateinit var eventCaptor: ArgumentCaptor<InvestmentSyncEvent>

    @BeforeEach
    fun setUp() {
        investmentDataProvider = mock()
        eventPublisher = mock()
        MockitoAnnotations.openMocks(this)
        service = SyncInvestmentsService(
            provider = investmentDataProvider,
            publisher = eventPublisher,
            symbolsList = listOf("AAPL", "MSFT"),
            kafkaTopic = "test-topic"
        )
    }

    @Test
    fun `should sync investments successfully for all symbols`() {
        // Given
        val aaplSymbol = StockSymbol("AAPL")
        val msftSymbol = StockSymbol("MSFT")

        val aaplQuote = StockQuote(
            symbol = "AAPL",
            price = BigDecimal("150.00"),
            previousClose = BigDecimal("149.00"),
            volume = 1000000L
        )
        val aaplFundamentals = StockFundamentals(
            symbol = "AAPL",
            pe = BigDecimal("25.00"),
            pb = BigDecimal("35.00")
        )

        val msftQuote = StockQuote(
            symbol = "MSFT",
            price = BigDecimal("300.00"),
            previousClose = BigDecimal("299.00"),
            volume = 500000L
        )
        val msftFundamentals = StockFundamentals(
            symbol = "MSFT",
            pe = BigDecimal("30.00"),
            pb = BigDecimal("40.00")
        )

        `when`(investmentDataProvider.fetchQuote(aaplSymbol)).thenReturn(aaplQuote)
        `when`(investmentDataProvider.fetchFundamentals(aaplSymbol)).thenReturn(aaplFundamentals)
        `when`(investmentDataProvider.fetchQuote(msftSymbol)).thenReturn(msftQuote)
        `when`(investmentDataProvider.fetchFundamentals(msftSymbol)).thenReturn(msftFundamentals)

        // When
        val result = service.sync()

        // Then
        assertEquals(2, result)
        verify(eventPublisher, times(2)).publish(
            eventCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("test-topic")
        )

        val capturedEvents = eventCaptor.allValues
        assertEquals(2, capturedEvents.size)
        assertEquals("AAPL", capturedEvents[0].symbol)
        assertEquals("MSFT", capturedEvents[1].symbol)
    }
}
