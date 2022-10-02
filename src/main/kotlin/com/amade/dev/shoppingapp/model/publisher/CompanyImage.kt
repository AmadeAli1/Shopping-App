package com.amade.dev.shoppingapp.model.publisher

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import javax.validation.constraints.NotNull

@Table("CompanyImage")
data class CompanyImage(
    @Id @Column("id") val id: Int? = null,
    @Column("path") var path: String? = null,
    @Column("imageUrl") var imageUrl: String? = null,
    @field:NotNull @Column("companyId") val companyId: UUID?,
) {
    constructor() : this(companyId = null)
}
