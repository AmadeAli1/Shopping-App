package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.model.user.DeliveryLocation
import com.amade.dev.shoppingapp.model.user.User
import com.amade.dev.shoppingapp.model.user.dto.UserDTO
import com.amade.dev.shoppingapp.service.user.UserService
import com.amade.dev.shoppingapp.utils.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RequestMapping("/api/account")
@RestController
class UserController(
    private val service: UserService,
) {
    @PostMapping("/register")
    suspend fun save(@Valid @RequestBody body: User): ResponseEntity<UserDTO> {
        val user = service.save(user = body)
        return ResponseEntity(user, HttpStatus.CREATED)
    }

    @GetMapping("/login")
    suspend fun login(
        @RequestParam("id", required = true) id: String,
    ): ResponseEntity<UserDTO> {
        val user = service.login(id)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/updateToken")
    suspend fun updateToken(
        @RequestParam("token", required = true) token: String,
        @RequestParam("userId", required = true) userId: String,
    ): ResponseEntity<ApiResponse<Boolean>> {
        return ResponseEntity(service.updateToken(token, userId), HttpStatus.OK)
    }

    @PostMapping("/delivery-location")
    suspend fun updateDeliveryLocation(
        @RequestBody @Valid location: DeliveryLocation,
    ): DeliveryLocation {
        return service.updateDelivery(location)
    }
}