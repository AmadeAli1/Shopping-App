package com.amade.dev.shoppingapp.model.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Table("UserLocation")
data class DeliveryLocation(
    @Id @Column("id") val id: Int? = null,
    @field:NotBlank @Column("user_fk") val userId: String,
    @field:NotBlank @Column("name") val name: String,
    @field:NotNull @Column("longitude") val longitude: Double,
    @field:NotNull @Column("latitude") val latitude: Double,
)
