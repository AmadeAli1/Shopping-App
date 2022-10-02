@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository.menu

import com.amade.dev.shoppingapp.model.menu.Product
import com.amade.dev.shoppingapp.model.menu.views.ProductView
import com.amade.dev.shoppingapp.pagination.PageMarkup
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : CoroutineCrudRepository<Product, UUID>, PageMarkup {

    @Modifying
    @Query("delete from product where uid = $1")
    suspend fun deleteProductById(id: UUID): Int

    @Query("select * from productview where uid = $1")
    suspend fun findProductViewById(id: UUID): ProductView?

    @Modifying
    @Query("DELETE from UserProductLike where userId=$1 and productId=$2")
    suspend fun removeLike(userId: String, productId: UUID): Int

    @Modifying
    @Query("update product set likes=likes+1 where uid=$1")
    suspend fun addLike(productId: UUID): Int

    @Modifying
    @Query("INSERT INTO UserProductLike (userId,productId) values (:?1,:?2)")
    suspend fun addUserLike(userId: String, productId: UUID): Int

    @Modifying
    @Query("update product set likes=likes-1 where uid=$1")
    suspend fun removeLike(productId: UUID): Int

    @Query("select exists(select * from UserProductLike where userId=$1 and productId=$2)")
    suspend fun verifyUserLike(userId: String, productId: UUID): Boolean

    @Query("select * from productview limit 20 offset :start")
    fun findByPage(start: Int): Flow<ProductView>

    @Query("select * from productview where upper(productview.name) like upper(concat($2,'%')) order by productview.name limit 20 offset :$1")
    fun findByPageWithName(start: Int, name: String): Flow<ProductView>

    @Query("select count(*) from product")
    override suspend fun total(): Long

    @Query("SELECT * FROM userproductlike WHERE userId=$1 and productId=$2")
    suspend fun findProductWithLike(userId: String, productId: UUID): Product.Like?

    @Query("select * from productview where upper(productview.name) like upper(concat($2,'%')) and categoryid=$3 order by productview.name limit 20 offset :$1")
    fun findByPageWithNameAndCategory(start: Int, name: String, categoryId: Int): Flow<ProductView>

    @Query("select * from productview where categoryid=$2 order by productview.name limit 20 offset :$1")
    fun findCategoryByPage(start: Int, categoryId: Int): Flow<ProductView>

    @Query("select * from userproductlikeview where userid=$2 limit 20 offset :$1")
    fun findByPageProductWithLikeByUserId(start: Int, userId: String): Flow<ProductView>

}