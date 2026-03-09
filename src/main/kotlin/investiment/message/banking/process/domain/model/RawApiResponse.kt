package investiment.message.banking.process.domain.model

data class RawApiResponse(
    val rawPayload: String,
    val provider: String
)
