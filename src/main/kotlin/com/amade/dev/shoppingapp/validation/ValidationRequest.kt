package com.amade.dev.shoppingapp.validation

import com.amade.dev.shoppingapp.exception.Message
import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import javax.validation.Validator

@Component
class ValidationRequest(
    private val validator: Validator,
    private val mapper: JsonMapper,
) {
    fun <T> isValid(request: T): ResponseEntity<out Any>? {

        val validate = validator.validate(request)
        if (validate.isNotEmpty()) {
            val erros = validate.map {
                Message(field = it.propertyPath.toString(), message = it.message)
            }.toList()
            return ResponseEntity(erros, HttpStatus.BAD_REQUEST)
        }
        return null
    }


}