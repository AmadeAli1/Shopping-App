package com.amade.dev.shoppingapp.model.publisher

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("CompanyImage")
data class CompanyImage(
    @Id @Column("id") val id: Int? = null,
    @Column("path") var path: String? = null,
    @Column("imageUrl") var imageUrl: String? = null,
    @Column("companyId") val companyId: UUID,
)
