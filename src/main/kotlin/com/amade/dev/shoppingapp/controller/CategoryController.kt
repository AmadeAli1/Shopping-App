package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.utils.ApiResponse
import com.amade.dev.shoppingapp.model.menu.Category
import com.amade.dev.shoppingapp.service.menu.CategoryService
import com.amade.dev.shoppingapp.validation.ValidationRequest
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/category")
@RestController
class CategoryController(
    private val service: CategoryService,
    private val validationRequest: ValidationRequest,
    private val mapper: JsonMapper,
) {

    @PostMapping("/save",consumes = [
        MediaType.MULTIPART_FORM_DATA_VALUE,
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_OCTET_STREAM_VALUE
    ])
    suspend fun save(
        @RequestPart("category", required = true) jsonBody: String,
        @RequestPart("image", required = true) image: FilePart,
    ): ResponseEntity<out Any>? {
        val request = try {
            mapper.readValue<Category>(jsonBody)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
        val isValid = validationRequest.isValid(request)
        if (isValid != null) {
            return isValid
        }
        val category = service.saveCategory(request, image)
        return ResponseEntity(category, HttpStatus.CREATED)
    }

    @DeleteMapping
    suspend fun delete(@RequestParam("id", required = true) id: Int): ApiResponse<Boolean> {
        return service.delete(id)
    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable("id") id: Int): ResponseEntity<out Any> {
        val category = service.findById(id)
        if (category != null) {
            return ResponseEntity(category, HttpStatus.OK)
        }
        return ResponseEntity("Category Not found",HttpStatus.BAD_REQUEST)
    }

    @GetMapping
    suspend fun findAll() = service.findAll()


}