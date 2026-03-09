package investiment.message.banking.process.infra.adapters.outbound

import com.fasterxml.jackson.databind.ObjectMapper
import investiment.message.banking.process.domain.exception.ExternalApiException
import investiment.message.banking.process.domain.ports.outbound.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Kafka adapter implementing the EventPublisher port.
 * This is an outbound adapter in hexagonal architecture,
 * connecting the domain to the Kafka infrastructure.
 */
@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    @Value("\${investment.kafka.topic}") private val defaultTopic: String
) : EventPublisher {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(payload: Any, topic: String?) {
        val targetTopic = topic ?: defaultTopic

        try {
            val jsonPayload = objectMapper.writeValueAsString(payload)

            kafkaTemplate.send(targetTopic, jsonPayload).get()
            logger.info("Event published to topic: {} - Payload: {}", targetTopic, jsonPayload)

        } catch (e: Exception) {
            logger.error("Error publishing event to topic: {} - {}", targetTopic, e.message, e)
            throw ExternalApiException("Failed to publish event to Kafka: ${e.message}", e)
        }
    }
}