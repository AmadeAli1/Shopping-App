package com.amade.dev.shoppingapp.model.publisher

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.format.annotation.NumberFormat
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Table("CompanyAddress")
data class CompanyAddress(
    @Id @Column("id") val id: Int? = null,
    @Column("companyId") val companyId: UUID?,
    @field:NotNull @Column("latitude") val latitude: Double,
    @field:NotNull @Column("longitude") val longitude: Double,
){
    constructor():this(companyId = null, latitude = 0.0, longitude = 0.0)
}
