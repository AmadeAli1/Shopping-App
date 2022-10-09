package com.amade.dev.shoppingapp.service.menu

import com.amade.dev.shoppingapp.cloud.StorageService
import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.menu.Product
import com.amade.dev.shoppingapp.model.menu.dto.ProductDto
import com.amade.dev.shoppingapp.pagination.Page
import com.amade.dev.shoppingapp.pagination.PageConfiguration
import com.amade.dev.shoppingapp.repository.menu.ProductRepository
import com.amade.dev.shoppingapp.utils.ApiResponse
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductService(
    private val storage: StorageService,
    private val repository: ProductRepository,
    private val categoryService: CategoryService,
) {

    suspend fun saveProduct(product: Product, image: FilePart): ProductDto {
        var saved: Product? = null
        var imageSaved: String? = null
        try {
            val category = categoryService.findById(product.categoryId)
            if (category != null) {
                saved = repository.save(product)
                val subDirectory = "company/${product.companyId}/product"
                imageSaved = storage.save(file = image, subDirectory)
                saved = repository.save(entity = saved.copy(path = imageSaved))
                return ProductDto(product = saved, category = category)
            }
            throw ApiException("Category Not found!!")
        } catch (e: Exception) {
            if (saved != null) {
                repository.delete(saved)
                if (imageSaved != null) {
                    storage.delete(filePath = imageSaved)
                }
            }
            throw ApiException(e.message)
        }
    }

    suspend fun deleteProductById(id: String): ApiResponse<Boolean> {
        val uuid = UUID.fromString(id)
        val product = repository.findById(uuid)
        if (product != null) {
            if (repository.deleteProductById(uuid) != 0) {
                storage.delete(filePath = product.path!!)
                return ApiResponse(true)
            }
            throw ApiException("Product not removed!!")
        }
        throw ApiException("Product not found!!")
    }

    suspend fun updateProduct(product: Product): Product {
        if (repository.existsById(product.uid!!)) {
            return repository.save(product)
        }
        throw ApiException("Failure to update Product. Because product with id: ${product.uid} : not exists!!")
    }

    suspend fun updateImageByPath(productId: String, filePath: String, image: FilePart): Product {
        val uid = UUID.fromString(productId)
        val product = repository.findById(uid)
        if (product != null) {
            val update = storage.update(filePath, image)
            return repository.save(entity = product.copy(path = update))
        }
        throw ApiException("Failure to update Image. Because product not found!!")
    }

    suspend fun incrementOrDecrementLike(userId: String, productId: String): ApiResponse<Boolean> {
        val pId = UUID.fromString(productId)
        return try {
            if (existsLike(userId, pId)) {
                val removeUserLike = repository.removeLike(userId = userId, pId)
                if (removeUserLike == 1) {
                    repository.removeLike(pId)
                    ApiResponse(false)
                } else {
                    throw ApiException(msg = "An error occurred to remove like")
                }
            } else {
                val addLike = repository.addUserLike(userId = userId, pId)
                if (addLike == 1) {
                    repository.addLike(pId)
                    ApiResponse(true)
                } else {
                    throw ApiException("An error occurred to add like!")
                }
            }
        } catch (e: Exception) {
            throw ApiException(e.message!!)
        }
    }

    private suspend fun existsLike(userId: String, productId: UUID): Boolean {
        return repository.verifyUserLike(userId = userId, productId = productId)
    }

    suspend fun findById(productId: String): ProductDto {
        val productView = repository.findProductViewById(UUID.fromString(productId))
        if (productView != null) {
            return productView.toProductDTO()
        }
        throw ApiException("Product with id {$productId} not found!!")
    }

    suspend fun getUserLike(userId: String, productId: String): ApiResponse<Boolean> {
        val id = UUID.fromString(productId)
        val isLike = repository.findProductWithLike(userId = userId, productId = id) != null
        return ApiResponse(isLike)
    }

    suspend fun findByPage(page: Int): Page<ProductDto> {
        val pageConfiguration = PageConfiguration<ProductDto>()
        lateinit var data: List<ProductDto>
        return pageConfiguration.config(repository = repository, page) { total, pages, start ->
            data = repository.findByPage(start = start).map { it.toProductDTO() }.toList()
            val next = (data.size == 20).and(total > page * 20)
            return@config pageConfiguration.getPage(
                data = data,
                pages = pages,
                totalItems = total,
                page = page,
                hasNext = next
            )
        }
    }

    suspend fun findByNameWithPagination(page: Int, name: String): Page<ProductDto> {
        val pageConfiguration = PageConfiguration<ProductDto>()
        lateinit var data: List<ProductDto>
        return pageConfiguration.config(repository = repository, page) { total, paginas, start ->
            data = repository.findByPageWithName(start = start, name = name).map { it.toProductDTO() }.toList()
            val next = (data.size == 20).and(total > page * 20)
            return@config pageConfiguration.getPage(
                data = data,
                pages = paginas,
                totalItems = total,
                page = page,
                hasNext = next
            )
        }
    }

    suspend fun searchByCategory(page: Int, categoryId: Int, name: String): Page<ProductDto> {
        val pageConfiguration = PageConfiguration<ProductDto>()
        lateinit var data: List<ProductDto>
        return pageConfiguration.config(repository = repository, page) { total, pages, start ->
            data = if (name.isBlank()) {
                repository.findCategoryByPage(start = start, categoryId = categoryId).map { it.toProductDTO() }.toList()
            } else {
                repository.findByPageWithNameAndCategory(
                    start = start, name = name, categoryId = categoryId
                ).map { it.toProductDTO() }.toList()
            }
            val next = (data.size == 20).and(total > page * 20)
            return@config pageConfiguration.getPage(
                data = data,
                pages = pages,
                totalItems = total,
                page = page,
                hasNext = next
            )
        }
    }

    suspend fun findAllProductWithLikeByUserUd(page: Int, userId: String): Page<ProductDto> {
        val pageConfiguration = PageConfiguration<ProductDto>()
        lateinit var data: List<ProductDto>
        return pageConfiguration.config(repository = repository, page) { total, paginas, start ->
            data = repository.findByPageProductWithLikeByUserId(start = start, userId = userId).map { it.toProductDTO() }
                .toList()
            val next = (data.size == 20).and(total > page * 20)
            return@config pageConfiguration.getPage(
                data = data,
                pages = paginas,
                totalItems = total,
                page = page,
                hasNext = next
            )
        }
    }

}