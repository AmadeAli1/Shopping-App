package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.menu.Product
import com.amade.dev.shoppingapp.model.menu.ProductCart
import com.amade.dev.shoppingapp.model.menu.dto.ProductDto
import com.amade.dev.shoppingapp.model.menu.views.ProductCartView
import com.amade.dev.shoppingapp.pagination.Page
import com.amade.dev.shoppingapp.service.menu.ProductService
import com.amade.dev.shoppingapp.service.menu.ShoppingCartService
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
    private val cartService: ShoppingCartService,
    private val productService: ProductService,
    private val mapper: JsonMapper,
    private val validationRequest: ValidationRequest,
) {

    @PostMapping(
        "/save",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE]
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
        val saved = productService.saveProduct(request, image)
        return ResponseEntity(saved, HttpStatus.CREATED)
    }

    @DeleteMapping
    suspend fun deleteProductById(@RequestParam("product", required = true) id: String): ApiResponse<Boolean> {
        return productService.deleteProductById(id)
    }


    @PutMapping("/update")
    suspend fun updateProduct(@RequestBody @Valid body: Product): Product {
        return productService.updateProduct(product = body)
    }

    @PutMapping(
        "/update/{productId}",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    suspend fun updateImageByPath(
        @PathVariable("productId", required = true) productId: String,
        @RequestParam("path", required = true) path: String,
        @RequestPart("image", required = true) image: FilePart,
    ): Product {
        return productService.updateImageByPath(productId, path, image)
    }

    @PostMapping("/like/{userId}/{productId}")
    suspend fun incrementOrDecrementLike(
        @PathVariable("userId", required = true) userId: String,
        @PathVariable("productId", required = true) productId: String,
    ): ApiResponse<Boolean> {
        return productService.incrementOrDecrementLike(userId, productId)
    }


    @GetMapping("/{id}")
    suspend fun findById(
        @PathVariable("id", required = true) productId: String,
    ): ProductDto {
        return productService.findById(productId)
    }

    @GetMapping
    suspend fun findWithPagination(
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("name", required = false, defaultValue = "") name: String,
    ): ResponseEntity<Page<ProductDto>> {
        val response: Page<ProductDto> = if (name.isBlank()) {
            productService.findByPage(page = page)
        } else {
            productService.findByNameWithPagination(page = page, name = name)
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/like/all")
    suspend fun findByPageProductWithLikeByUserId(
        @RequestParam("page", defaultValue = "1") page: Int,
        @RequestParam("userId", required = true) userId: String,
    ): Page<ProductDto> {
        return productService.findAllProductWithLikeByUserUd(page, userId)
    }

    @GetMapping("/like/{userId}/{productId}")
    suspend fun findAllLikes(
        @PathVariable("userId", required = true) userId: String,
        @PathVariable("productId", required = true) productId: String,
    ): ApiResponse<Boolean> {
        return productService.getUserLike(userId, productId = productId)
    }

    @GetMapping("/category")
    suspend fun findCartByUserId(
        @RequestParam(name = "id", required = true) id: Int,
        @RequestParam(name = "page", defaultValue = "1") page: Int,
        @RequestParam(name = "name", required = false, defaultValue = "") name: String,
    ): ResponseEntity<Page<ProductDto>> {
        val response = productService.searchByCategory(page = page, categoryId = id, name = name)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/shopping-cart")
    suspend fun addToCart(@RequestBody @Valid cart: ProductCart): ApiResponse<Boolean> {
        return cartService.addProductToCart(cart)
    }

    @DeleteMapping("/shopping-cart")
    suspend fun removeFromCart(
        @RequestParam("userId", required = true) userId: String,
        @RequestParam("productId", required = true) productId: String,
    ): ApiResponse<Boolean> {
        return cartService.removeProductCart(userId, productId)
    }

    @GetMapping("/shopping-cart")
    suspend fun findCartByUserId(
        @RequestParam(name = "userId", required = true) id: String,
        @RequestParam(name = "page", defaultValue = "1") page: Int,
    ): ResponseEntity<Page<ProductCartView>> {
        val response = cartService.getCartByUserId(userId = id, page = page)
        return ResponseEntity(response, HttpStatus.OK)
    }

}