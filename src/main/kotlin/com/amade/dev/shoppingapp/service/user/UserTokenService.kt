package com.amade.dev.shoppingapp.service.user

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.user.UserToken
import com.amade.dev.shoppingapp.repository.user.TokenRepository
import com.amade.dev.shoppingapp.repository.user.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UserTokenService(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
) {

    suspend fun save(userId: String): UUID {
        val userToken = tokenRepository.save(entity = UserToken(userId = userId))
        return userToken.uid
    }

    suspend fun isTokenVerified(userId: String): Boolean {
        return tokenRepository.findByUserIdAndValid(userId, true) != null
    }

    suspend fun confirmToken(tokenId: String): ResponseEntity<out Any> {
        val tokenUUID = UUID.fromString(tokenId)

        val token = tokenRepository.findByUidAndValid(
            uid = tokenUUID,
            valid = true
        )
        if (token == null) {
            throw ApiException("This token not exists")
        } else {
            if (token.confirmedAt != null) {
                throw ApiException("The Token has already been confirmed!")
            }

            val currentTimer = LocalDateTime.now()

            return if (token.expiredAt.isBefore(currentTimer)) {
                throw ApiException("The Token has expired")
            } else {
                val user = userRepository.findById(id = token.userId)!!
                token.confirmedAt = currentTimer
                token.valid = false
                userRepository.save(user)
                tokenRepository.save(entity = token)
                ResponseEntity.ok("Successful account confirmation!")
            }

        }
    }


}