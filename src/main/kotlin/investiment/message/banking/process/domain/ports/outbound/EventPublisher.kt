package investiment.message.banking.process.domain.ports.outbound

/**
 * Port for publishing domain events to external systems (e.g., message brokers).
 * This is an outbound port (dependency) in hexagonal architecture.
 */
interface EventPublisher {

    /**
     * Publishes an investment event to the message broker.
     *
     * @param payload The event payload to publish
     * @param topic Optional topic/channel name. If not provided, uses default topic
     * @throws investiment.message.banking.process.domain.exception.InvestmentException if publishing fails
     */
    fun publish(payload: Any, topic: String? = null)
}