package com.amade.dev.shoppingapp.model.menu.dto

import com.amade.dev.shoppingapp.model.menu.Category
import com.amade.dev.shoppingapp.model.menu.Product

data class ProductDto(
    val product: Product,
    val category: Category,
)
