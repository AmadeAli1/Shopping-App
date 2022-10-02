package com.amade.dev.shoppingapp.service.menu

import com.amade.dev.shoppingapp.cloud.StorageService
import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.utils.ApiResponse
import com.amade.dev.shoppingapp.model.menu.Category
import com.amade.dev.shoppingapp.repository.menu.CategoryRepository
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val repository: CategoryRepository,
    private val storageService: StorageService,
) {

    suspend fun saveCategory(category: Category, image: FilePart): Category {
        var saved: Category? = null
        var path: String? = null
        try {
            saved = repository.save(category)
            path = storageService.save(image, "category")
            return repository.save(saved.copy(path = path))
        } catch (e: Exception) {
            if (saved != null) {
                repository.delete(saved)
                if (path != null) {
                    storageService.delete(path)
                }
            }
            throw ApiException(e.message)
        }
    }

    suspend fun delete(id: Int): ApiResponse<Boolean> {
        val category = findById(id)
        if (category != null) {
            if (repository.deleteCategoryById(id) != 0) {
                storageService.delete(category.path!!)
                return ApiResponse(true)
            }
            throw ApiException("Category not removed!!")
        }
        throw ApiException("Category not found!!")
    }

    suspend fun findById(id: Int): Category? {
        return repository.findById(id)
    }

    suspend fun findAll() = repository.findAll()

}