package com.amade.dev.shoppingapp.model.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Table("Usuario")
data class User(
    @Id @Column("id") val id: String? = null,
    @field:NotBlank @Column("username") val username: String,
    @field:NotBlank @field:Email @Column("email") val email: String,
    @Column("notificationToken") val notificationToken: String? = null,
    @Column("cityName") val city: City?,
    @Column("mobileNumber") var cellphone: String? = null,
)
