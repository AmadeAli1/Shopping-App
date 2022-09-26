@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository.user

import com.amade.dev.shoppingapp.model.user.DeliveryLocation
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeliveryLocationRepository : CoroutineCrudRepository<DeliveryLocation, Int> {

    suspend fun findByUserId(userId: String):DeliveryLocation?

    @Modifying
    @Query("DELETE FROM location WHERE user_fk =$1")
    suspend fun deleteByUserId(userId: String): Int

}