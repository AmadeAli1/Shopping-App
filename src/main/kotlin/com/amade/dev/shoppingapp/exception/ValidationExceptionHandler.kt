package com.amade.dev.shoppingapp.exception
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException

@ControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun isValid(e: WebExchangeBindException): ResponseEntity<List<Message>> {
        val errors = e.bindingResult.allErrors.stream().map {
            lateinit var error: Message
            if (it is FieldError) {
                error = Message(field = it.field, message = it.defaultMessage!!)
            }
            error
        }.toList()
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

}