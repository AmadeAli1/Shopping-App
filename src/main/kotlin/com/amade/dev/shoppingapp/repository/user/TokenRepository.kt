@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository.user

import com.amade.dev.shoppingapp.model.user.UserToken
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenRepository : CoroutineCrudRepository<UserToken, UUID> {

    suspend fun findByUidAndValid(uid: UUID, valid: Boolean): UserToken?

    suspend fun findByUserIdAndValid(userId: String, valid: Boolean): UserToken?
}