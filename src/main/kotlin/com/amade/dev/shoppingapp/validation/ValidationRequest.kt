package com.amade.dev.shoppingapp.validation

import com.amade.dev.shoppingapp.exception.Message
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import javax.validation.Validator

@Component
class ValidationRequest(
    private val validator: Validator,
) {
    fun <T> isValid(request: T): ResponseEntity<out Any>? {
        val validate = validator.validate(request)
        if (validate.isNotEmpty()) {
            val errors = validate.map {
                Message(field = it.propertyPath.toString(), message = it.message)
            }.toList()
            return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
        }
        return null
    }


}