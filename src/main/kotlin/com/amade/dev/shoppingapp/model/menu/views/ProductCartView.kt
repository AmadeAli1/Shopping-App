package com.amade.dev.shoppingapp.model.menu.views

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("ProductCartView")
data class ProductCartView(
    @Column("name") val name: String,
    @Column("price") val price: Float,
    @Column("imageUrl") val imageUrl: String,
    @Column("cartId") val cartId: Int,
    @Column("userId") val userId: String,
    @Column("productId") val productId: UUID,
)
