package com.amade.dev.shoppingapp.controller

import com.amade.dev.shoppingapp.model.user.User
import com.amade.dev.shoppingapp.model.user.dto.UserDTO
import com.amade.dev.shoppingapp.service.UserService
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
        @RequestParam("email", required = true) email: String,
        @RequestParam("password", required = true) password: String,
    ): ResponseEntity<UserDTO> {
        val user = service.login(email, password)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/{id}")
    suspend fun getUser(@PathVariable("id") id: String): ResponseEntity<out Any> {
        return service.getUser(id)
    }

    @GetMapping("/confirmation")
    suspend fun confirmToken(@RequestParam("token", required = true) token: String): ResponseEntity<out Any> {
        return service.confirmToken(token)
    }


}