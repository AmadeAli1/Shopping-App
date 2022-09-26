package com.amade.dev.shoppingapp.model.publisher

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import javax.validation.constraints.NotBlank

@Table("CompanyAddressRepository")
data class CompanyAddress(
    @Id @Column("id") val id: Int? = null,
    @field:NotBlank @Column("companyId") val companyId: UUID,
    @field:NotBlank @Column("latitude") val latitude: Double,
    @field:NotBlank @Column("longitude") val longitude: Double,
)
