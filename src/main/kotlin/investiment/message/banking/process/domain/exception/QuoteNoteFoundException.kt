package investiment.message.banking.process.domain.exception

sealed class InvestmentException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class QuoteNotFoundException(symbol: String) : InvestmentException("Quote not found for symbol: $symbol")

class FundamentalsNotFoundException(symbol: String) : InvestmentException("Fundamentals not found for symbol: $symbol")

class ExternalApiException(message: String, cause: Throwable? = null) : InvestmentException("External API error: $message", cause)

class DataMappingException(message: String, cause: Throwable? = null) : InvestmentException("Data mapping error: $message", cause)
