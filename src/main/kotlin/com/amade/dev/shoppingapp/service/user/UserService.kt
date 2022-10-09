package com.amade.dev.shoppingapp.service.user

import com.amade.dev.shoppingapp.exception.ApiException
import com.amade.dev.shoppingapp.model.user.DeliveryLocation
import com.amade.dev.shoppingapp.model.user.User
import com.amade.dev.shoppingapp.model.user.dto.UserDTO
import com.amade.dev.shoppingapp.repository.user.DeliveryLocationRepository
import com.amade.dev.shoppingapp.repository.user.UserRepository
import com.amade.dev.shoppingapp.utils.ApiResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val locationRepository: DeliveryLocationRepository,
) {

    suspend fun save(user: User): UserDTO {

        if (!existsByEmail(user.email)) {
            val insert = userRepository.insert(
                id = user.id!!,
                email = user.email,
                username = user.username,
                cityname = if (user.city == null) null else user.city.name,
                mobile = user.cellphone,
            )
            return if (insert == 1) {
                val saved = findById(user.id)!!
                val location = getLocationDelivery(saved.id!!)
                UserDTO(user, location)
            } else {
                throw ApiException("An error occurred")
            }
        }

        val existsById = existsById(user.id!!)
        if (existsById) {
            throw ApiException("This id already in use!!")
        }

        throw ApiException("This email already exists")
    }

    suspend fun login(id: String): UserDTO {
        val user = findById(id)
        if (user != null) {
            val location = getLocationDelivery(user.id!!)
            return UserDTO(user, location)

        } else {
            throw ApiException("User not found")
        }
    }

    suspend fun updateToken(token: String, userId: String): ApiResponse<Boolean> {
        try {
            val result = userRepository.updateToken(token, userId) != 0
            return ApiResponse(result)
        } catch (e: Exception) {
            throw ApiException(e.message)
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

    private suspend fun existsByEmail(email: String) = userRepository.existsByEmail(email)

    private suspend fun existsById(id: String) = userRepository.existsById(id)
    private suspend fun findById(id: String) = userRepository.findById(id)

    private suspend fun getLocationDelivery(userId: String) = locationRepository.findByUserId(userId)
    suspend fun updateDelivery(location: DeliveryLocation): DeliveryLocation {
        val dl = locationRepository.findByUserId(location.userId)
        if (dl != null) {
            val deliveryLocation =
                dl.copy(latitude = location.latitude, longitude = location.longitude, name = location.name)
            return locationRepository.save(deliveryLocation)
        }
        return locationRepository.save(entity = location)
    }

}