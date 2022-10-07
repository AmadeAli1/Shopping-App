@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository.menu

import com.amade.dev.shoppingapp.model.menu.Category
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CoroutineCrudRepository<Category, Int> {

    @Modifying
    @Query("delete from category where id = $1")
    suspend fun deleteCategoryById(id: Int): Int


}