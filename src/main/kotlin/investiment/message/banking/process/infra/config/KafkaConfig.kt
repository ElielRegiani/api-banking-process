package investiment.message.banking.process.infra.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

/**
 * Kafka configuration for the application.
 * Sets up serialization, ObjectMapper, and other Kafka-related beans.
 */
@Configuration
@EnableKafka
class KafkaConfig {

    /**
     * Configures ObjectMapper for JSON serialization/deserialization.
     * Includes Java 8 date/time support and proper formatting.
     */
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}