package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.model.publisher.CompanyAddress
import com.amade.dev.shoppingapp.model.publisher.dto.CompanyDTO
import com.amade.dev.shoppingapp.service.publisher.CompanyService
import com.amade.dev.shoppingapp.validation.ValidationRequest
import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/company")
@RestController
class CompanyController(
    private val service: CompanyService,
    private val validation: ValidationRequest,
    private val mapper: JsonMapper,
) {

    @PostMapping("/register", consumes = [MediaType.ALL_VALUE])
    suspend fun register(
        @RequestPart("company") jsonCompany: String,
        @RequestPart("address") jsonAddress: String,
        @RequestPart("file") filePart: FilePart,
    ): ResponseEntity<out Any>? {
        val company = try {
            mapper.readValue(jsonCompany, Company::class.java)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }

        val address = try {
            mapper.readValue(jsonAddress, CompanyAddress::class.java)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
        val companyValidation = validation.isValid(company)
        if (companyValidation != null) {
            return companyValidation
        }
        val addressValidation = validation.isValid(address)
        if (addressValidation != null) {
            return addressValidation
        }
        val companyDTO = service.save(companyBody = company, addressBody = address, logoImage = filePart)
        return ResponseEntity(companyDTO, HttpStatus.CREATED)
    }

    @GetMapping("/login")
    suspend fun login(
        @RequestParam("email", required = true) email: String,
        @RequestParam("password", required = true) password: String,
    ): CompanyDTO {
        return service.login(email, password)
    }

    @DeleteMapping
    suspend fun deleteImage(
        @RequestParam("path", required = true) path: String,
    ): Boolean {
        return service.deleteImage(path)
    }


}
