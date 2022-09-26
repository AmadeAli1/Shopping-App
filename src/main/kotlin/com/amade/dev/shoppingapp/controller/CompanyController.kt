package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.service.publisher.CompanyService
import com.amade.dev.shoppingapp.validation.ValidationRequest
import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/company")
@RestController
class CompanyController(
    private val service: CompanyService,
    private val validation: ValidationRequest,
    private val mapper: JsonMapper,
) {

    @PostMapping("/register", consumes = [MediaType.ALL_VALUE])
    suspend fun register(
        @RequestPart("body") jsonBody: String,
        @RequestPart("file") filePart: FilePart,
    ): ResponseEntity<out Any>? {
        val request = try {
            mapper.readValue(jsonBody, Company::class.java)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
        val result = validation.isValid(request)
        if (result != null) {
            return result
        }
        val companyDTO = service.save(body = request, logoImage = filePart)
        return ResponseEntity(companyDTO, HttpStatus.CREATED)
    }

}
