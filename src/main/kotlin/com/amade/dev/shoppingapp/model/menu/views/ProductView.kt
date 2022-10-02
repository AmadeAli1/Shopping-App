package com.amade.dev.shoppingapp.model.menu.views

import com.amade.dev.shoppingapp.model.menu.Category
import com.amade.dev.shoppingapp.model.menu.Product
import com.amade.dev.shoppingapp.model.menu.dto.ProductDto
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@Table("ProductView")
data class ProductView(
    @Column("uid") val uid: UUID? = null,
    @Column("discount") val discount: Float? = null,
    @Column("imageUrl") val imageUrl: String? = null,
    @Column("state") var state: Product.State = Product.State.Available,
    @Column("deliveryState") val deliveryState: Product.State? = null,
    @field:NotBlank @Column("name") val name: String,
    @field:NotNull @field:Positive @Column("price") val price: Float,
    @Column("likes") val likes: Int,
    @field:NotBlank @Column("description") val description: String,
    @field:NotNull @Column("categoryId") val categoryId: Int,
    @field:NotNull @Column("companyId") val companyId: UUID?,
    @Column("categoryName") val categoryName: String,
    @Column("categoryImageUrl") var categoryImageUrl: String?,
    @Column("total") var totalProducts: Int = 0,
) {

    fun toProductDTO(): ProductDto {
        return ProductDto(
            product = Product(
                uid,
                discount,
                state,
                deliveryState,
                name,
                price,
                likes,
                imageUrl,
                description,
                categoryId,
                companyId
            ),
            category = Category(categoryId, categoryName, categoryImageUrl, totalProducts)
        )
    }

}
