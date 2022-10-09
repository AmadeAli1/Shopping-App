package com.amade.dev.shoppingapp.model.menu.views

import com.amade.dev.shoppingapp.model.menu.Category
import com.amade.dev.shoppingapp.model.menu.Product
import com.amade.dev.shoppingapp.model.menu.dto.ProductDto
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("ProductView")
data class ProductView(
    @Column("uid") val uid: UUID? = null,
    @Column("discount") val discount: Float? = null,
    @Column("imageUrl") val imageUrl: String? = null,
    @Column("cookTime") val cookTime: String,
    @Column("available") var state: Product.State = Product.State.Available,
    @Column("description") val description: String,
    @Column("deliveryState") val deliveryState: Product.State? = null,
    @Column("name") val name: String,
    @Column("price") val price: Float,
    @Column("likes") val likes: Int,
    @Column("unlikes") val unlikes: Int,
    @Column("companyId") val companyId: UUID?,
    @Column("categoryId") val categoryId: Int,
    @Column("categoryName") val categoryName: String,
    @Column("categoryImageUrl") var categoryImageUrl: String?,
    @Column("categoryDescription") val categoryDescription: String?,
    @Column("total") var totalProducts: Int = 0,
) {

    fun toProductDTO(): ProductDto {
        return ProductDto(
            product = Product(
                uid,
                discount,
                state,
                cookTime = cookTime,
                deliveryState,
                name,
                price,
                likes,
                unlikes,
                imageUrl,
                description,
                categoryId,
                companyId
            ),
            category = Category(categoryId, categoryName, "", categoryImageUrl, totalProducts)
        )
    }

}
