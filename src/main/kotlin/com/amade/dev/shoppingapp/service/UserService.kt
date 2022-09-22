package com.amade.dev.shoppingapp.service

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.user.User
import com.amade.dev.shoppingapp.model.user.dto.UserDTO
import com.amade.dev.shoppingapp.repository.DeliveryLocationRepository
import com.amade.dev.shoppingapp.repository.UserRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val locationRepository: DeliveryLocationRepository,
    private val emailService: EmailService,
    private val userTokenService: UserTokenService,
) {

    @Value(value = "\${url.confirmation.token}")
    val confirmTokenUrl: String? = null

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun save(user: User): UserDTO {

        val existsById = existsById(user.id!!)
        println(existsById)
        if (existsById){
            throw ApiException("This id already in use!!")
        }

        if (!existsByEmail(user.email)) {
            val insert = userRepository.insert(
                id = user.id,
                email = user.email,
                username = user.username,
                cityname = user.cityname,
                mobile = user.cellphone,
                isenable = true
            )
            return if (insert == 1) {
                val saved = findById(user.id)!!
                val token = userTokenService.save(saved.id!!)
                GlobalScope.launch { sendConfirmationToken(saved, token) }
                val location = getLocationDelivery(saved.id)
                UserDTO(user, location)
            } else {
                throw ApiException("An error occurred")
            }
        }

        throw ApiException("This email already exists")
    }

    suspend fun login(id: String): UserDTO {
        val user = findById(id)
        if (user != null) {
            if (!user.isEnable) {
                if (!userTokenService.isTokenVerified(userId = user.id!!)) {
                    val token = userTokenService.save(user.id)
                    sendConfirmationToken(user, token)
                    throw ApiException("Your account is not enable, open your email inbox and confirm your account!")
                }
                throw ApiException("Your account is not enable, open your email inbox and confirm your account!")
            } else {
                val location = getLocationDelivery(user.id!!)
                return UserDTO(user, location)
            }
        } else {
            throw ApiException("User not found")
        }
    }

    suspend fun update(user: User): UserDTO {
        return try {
            val saved = userRepository.save(entity = user)
            val location = getLocationDelivery(saved.id!!)
            UserDTO(saved, location)
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
    }

    suspend fun confirmToken(token: String) = userTokenService.confirmToken(token)
    private suspend fun existsByEmail(email: String) = userRepository.existsByEmail(email)

    private suspend fun existsById(id: String) = userRepository.existsById(id)
    private suspend fun findById(id: String) = userRepository.findById(id)

    private suspend fun sendConfirmationToken(user: User, token: UUID) {
        emailService.sendEmail(
            sendToEmail = user.email, subject = "Confirm account", body = """
                        Hi ${user.username} thank you for your account at Market
                        To confirm your account: ${confirmTokenUrl}?token=${token}
                        Valid Token for 3 HOURS!
                    """.trimIndent()
        )
    }

    private suspend fun getLocationDelivery(userId: String) = locationRepository.findByUserId(userId)

}