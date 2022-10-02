package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.menu.Product
import com.amade.dev.shoppingapp.model.menu.dto.ProductDto
import com.amade.dev.shoppingapp.pagination.Page
import com.amade.dev.shoppingapp.service.menu.ProductService
import com.amade.dev.shoppingapp.utils.ApiResponse
import com.amade.dev.shoppingapp.validation.ValidationRequest
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RequestMapping("/api/product")
@RestController
class ProductController(
    private val service: ProductService,
    private val mapper: JsonMapper,
    private val validationRequest: ValidationRequest,
) {

    @PostMapping(
        "/save", consumes = [
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE
        ]
    )
    suspend fun save(
        @RequestPart("product") jsonBody: String,
        @RequestPart("image") image: FilePart,
    ): ResponseEntity<out Any>? {

        val request = try {
            mapper.readValue<Product>(jsonBody)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
        val isValid = validationRequest.isValid(request)
        if (isValid != null) {
            return isValid
        }
        val saved = service.saveProduct(request, image)
        return ResponseEntity(saved, HttpStatus.CREATED)
    }

    @DeleteMapping
    suspend fun deleteProductById(@RequestParam("product", required = true) id: String): ApiResponse<Boolean> {
        return service.deleteProductById(id)
    }


    @PutMapping("/update")
    suspend fun updateProduct(@RequestBody @Valid body: Product): Product {
        return service.updateProduct(product = body)
    }

    @PutMapping(
        "/update/{productId}", consumes = [
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE
        ]
    )
    suspend fun updateImageByPath(
        @PathVariable("productId", required = true) productId: String,
        @RequestParam("path", required = true) path: String,
        @RequestPart("image", required = true) image: FilePart,
    ): Product {
        return service.updateImageByPath(productId, path, image)
    }

    @PostMapping("/like/{userId}/{productId}")
    suspend fun incrementOrDecrementLike(
        @PathVariable("userId", required = true) userId: String,
        @PathVariable("productId", required = true) productId: String,
    ): ApiResponse<Boolean> {
        return service.incrementOrDecrementLike(userId, productId)
    }


    @GetMapping("/{id}")
    suspend fun findById(
        @PathVariable("id", required = true) productId: String,
    ): ProductDto {
        return service.findById(productId)
    }

    @GetMapping
    suspend fun findWithPagination(
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("name", required = false, defaultValue = "") name: String,
    ): ResponseEntity<Page<ProductDto>> {
        val response: Page<ProductDto> = if (name.isBlank()) {
            service.findByPage(page = page)
        } else {
            service.findByNameWithPagination(page = page, name = name)
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/like/all")
    suspend fun findByPageProductWithLikeByUserId(
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("userId", required = true) userId: String,
    ): Page<ProductDto> {
        return service.findAllProductWithLikeByUserUd(page, userId)
    }

    @GetMapping("/like/{userId}/{productId}")
    suspend fun findAllLikes(
        @PathVariable("userId", required = true) userId: String,
        @PathVariable("productId", required = true) productId: String,
    ): ApiResponse<Boolean> {
        return service.getUserLike(userId, productId = productId)
    }

    @GetMapping("/category")
    suspend fun findProductByCategory(
        @RequestParam(name = "id", required = true) id: Int,
        @RequestParam(name = "page", defaultValue = "1") page: Int,
        @RequestParam(name = "name", required = false, defaultValue = "") name: String,
    ): ResponseEntity<Page<ProductDto>> {
        val response = service.searchByCategory(page = page, categoryId = id, name = name)
        return ResponseEntity(response, HttpStatus.OK)
    }

}