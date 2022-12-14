@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.dev.shoppingapp.repository

import com.amade.dev.shoppingapp.model.user.User
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<User, String> {

    @Modifying
    @Query("INSERT INTO usuario (id, email, username, password, cityname, mobilenumber, isenable) VALUES (:?1,:?2,:?3,:?4,:?5,:?6,:?7)")
    suspend fun insert(
        id: String,
        email: String,
        username: String,
        password: String,
        cityname: String,
        mobile: String?,
        isenable: Boolean,
    ): Int

    @Modifying
    @Query("DELETE FROM USUARIO WHERE id=:$1")
    suspend fun deleteUserById(id: String): Int

    suspend fun existsByEmail(email: String): Boolean

    suspend fun findByEmail(email: String): User?

}