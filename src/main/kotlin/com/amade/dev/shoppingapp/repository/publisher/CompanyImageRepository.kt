@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository.publisher

import com.amade.dev.shoppingapp.model.publisher.CompanyImage
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompanyImageRepository : CoroutineCrudRepository<CompanyImage, Int> {

    fun getAllByCompanyId(companyId: UUID): Flow<CompanyImage>

    @Modifying
    @Query("delete from companyimage where path = $1")
    suspend fun deleteImageByPath(filePath: String): Int


}