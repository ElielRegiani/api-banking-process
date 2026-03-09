package investiment.message.banking.process.infra.exception

import investiment.message.banking.process.domain.exception.InvestmentException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import java.util.UUID

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(InvestmentException::class)
    fun handleInvestmentException(e: InvestmentException): ResponseEntity<ErrorResponse> {
        logger.warn("Investment exception: {}", e.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    errorId = UUID.randomUUID().toString(),
                    message = e.message ?: "Investment processing failed",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.BAD_REQUEST.value()
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected exception: {}", e.message, e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    errorId = UUID.randomUUID().toString(),
                    message = "An unexpected error occurred",
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value()
                )
            )
    }
}

data class ErrorResponse(
    val errorId: String,
    val message: String,
    val timestamp: LocalDateTime,
    val status: Int
)
