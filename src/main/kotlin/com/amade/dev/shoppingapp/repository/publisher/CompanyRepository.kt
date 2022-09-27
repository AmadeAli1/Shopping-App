@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository.publisher

import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.model.publisher.views.CompanyView
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompanyRepository : CoroutineCrudRepository<Company, UUID> {

    suspend fun existsByEmail(email: String): Boolean

    suspend fun findByEmail(email: String): Company?

    @Query("select * from companyview where email = $1")
    suspend fun getById(email: String): CompanyView?

    @Modifying
    @Query("delete from company where uid = $1")
    suspend fun deleteByUid(uid: UUID): Int

}