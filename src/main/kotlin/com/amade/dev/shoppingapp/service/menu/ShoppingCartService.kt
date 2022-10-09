package com.amade.dev.shoppingapp.service.menu

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.menu.ProductCart
import com.amade.dev.shoppingapp.model.menu.views.ProductCartView
import com.amade.dev.shoppingapp.pagination.Page
import com.amade.dev.shoppingapp.pagination.PageConfiguration
import com.amade.dev.shoppingapp.repository.menu.ProductCartRepository
import com.amade.dev.shoppingapp.utils.ApiResponse
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShoppingCartService(
    private val cartRepository: ProductCartRepository,
) {

    suspend fun addProductToCart(cart: ProductCart): ApiResponse<Boolean> {
        try {
            if (cartRepository.existsByProductIdAndUserId(cart.productId, cart.userId)) {
                return ApiResponse(false)
            }
            cartRepository.save(entity = cart)
            return ApiResponse(true)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
    }

    suspend fun removeProductCart(userId: String, productId: String): ApiResponse<Boolean> {
        try {
            val remove =
                cartRepository.deleteByProductIdAndUserId(productId = UUID.fromString(productId), userId = userId) != 0
            return ApiResponse(remove)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
    }

    suspend fun getCartByUserId(userId: String, page: Int): Page<ProductCartView> {
        val countByUserId = cartRepository.countByUserId(userId)
        val pageConfiguration = PageConfiguration<ProductCartView>()
        lateinit var data: List<ProductCartView>
        return pageConfiguration.config(total = countByUserId, page) { total, pages, start ->
            data = cartRepository.getByUserId(start = start, userId = userId).toList()
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

}