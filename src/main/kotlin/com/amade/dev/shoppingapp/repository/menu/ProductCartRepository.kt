@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository.menu

import com.amade.dev.shoppingapp.model.menu.ProductCart
import com.amade.dev.shoppingapp.model.menu.views.ProductCartView
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductCartRepository : CoroutineCrudRepository<ProductCart, Int> {

    suspend fun existsByProductIdAndUserId(productId: UUID, userId: String): Boolean

    suspend fun deleteByProductIdAndUserId(productId: UUID, userId: String): Int

    @Query("select * from productcartview  where userid=$2 limit 20 offset :$1")
    fun getByUserId(start: Int, userId: String): Flow<ProductCartView>

    @Query("select count(id) from productcart where userid=($1)")
    suspend fun countByUserId(userId: String): Long

}