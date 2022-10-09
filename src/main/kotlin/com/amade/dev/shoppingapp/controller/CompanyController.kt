package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.model.publisher.CompanyAddress
import com.amade.dev.shoppingapp.model.publisher.CompanyImage
import com.amade.dev.shoppingapp.model.publisher.dto.CompanyDTO
import com.amade.dev.shoppingapp.pagination.Page
import com.amade.dev.shoppingapp.service.publisher.CompanyService
import com.amade.dev.shoppingapp.utils.ApiResponse
import com.amade.dev.shoppingapp.validation.ValidationRequest
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RequestMapping("/api/company")
@RestController
class CompanyController(
    private val service: CompanyService,
    private val validation: ValidationRequest,
    private val mapper: JsonMapper,
) {

    @PostMapping(
        "/register", consumes = [
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE
        ]
    )
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

    @DeleteMapping("/{email}/{password}")
    suspend fun deleteAccount(
        @PathVariable("email") email: String,
        @PathVariable("password") password: String,
    ): ApiResponse<String> {
        return service.deleteCompany(email, password)
    }

    @PostMapping("/upload/images")
    suspend fun uploadCompanyImages(
        @RequestPart("body") jsonBody: String,
        @RequestPart("images") images: Flux<FilePart>,
    ): ResponseEntity<out Any>? {
        val request = try {
            mapper.readValue<CompanyImage>(jsonBody)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }

        val isValid = validation.isValid(request)
        if (isValid != null) {
            return isValid
        }
        val companyImages = service.saveCompanyImages(
            request, withContext(Dispatchers.IO) { images.toStream() }.toList()
        )
        return ResponseEntity(companyImages, HttpStatus.CREATED)
    }

    @DeleteMapping
    suspend fun deleteCompanyImage(@RequestParam("path", required = true) path: String): ApiResponse<Boolean> {
        return service.deleteCompanyImage(path)
    }

    @GetMapping
    suspend fun findWithPagination(
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("name", required = false, defaultValue = "") name: String,
    ): ResponseEntity<Page<CompanyDTO>> {
        val response: Page<CompanyDTO> = if (name.isBlank()) {
            service.findAnyPage(page = page)
        } else {
            service.findPageByName(page = page, name = name)
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

}
