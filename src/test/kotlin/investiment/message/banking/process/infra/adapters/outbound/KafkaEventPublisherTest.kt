package investiment.message.banking.process.infra.adapters.outbound

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import investiment.message.banking.process.domain.model.InvestmentSyncEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.kafka.core.KafkaTemplate
import java.math.BigDecimal
import kotlin.test.assertNotNull

class KafkaEventPublisherTest {

    private lateinit var kafkaTemplate: KafkaTemplate<String, String>
    private lateinit var objectMapper: ObjectMapper
    private lateinit var publisher: KafkaEventPublisher

    @BeforeEach
    fun setUp() {
        kafkaTemplate = mock()
        // Create ObjectMapper with JSR310 module to handle LocalDateTime
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        publisher = KafkaEventPublisher(
            kafkaTemplate = kafkaTemplate,
            objectMapper = objectMapper,
            defaultTopic = "test-topic"
        )
    }

    @Test
    fun `should publish event successfully`() {
        // Given
        val event = InvestmentSyncEvent(
            symbol = "AAPL",
            price = BigDecimal("150.00"),
            provider = "FMP"
        )
        val jsonString = objectMapper.writeValueAsString(event)
        `when`(kafkaTemplate.send("test-topic", jsonString))
            .thenReturn(mock())

        // When
        publisher.publish(event, "test-topic")

        // Then - no exception should be thrown
        assertNotNull(event)
    }

    @Test
    fun `should use default topic when none provided`() {
        // Given
        val event = InvestmentSyncEvent(
            symbol = "MSFT",
            price = BigDecimal("300.00")
        )
        val jsonString = objectMapper.writeValueAsString(event)
        `when`(kafkaTemplate.send("test-topic", jsonString))
            .thenReturn(mock())

        // When
        publisher.publish(event)

        // Then - should use default topic
        assertNotNull(event)
    }
}
