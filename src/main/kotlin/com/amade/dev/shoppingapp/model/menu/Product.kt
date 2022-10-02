package com.amade.dev.shoppingapp.model.menu

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@Table("Product")
data class Product(
    @Id @Column("uid") val uid: UUID? = null,
    @Column("discount") val discount: Float? = null,
    @Column("state") var state: State = State.Available,
    @Column("deliveryState") val deliveryState: State? = State.Available,
    @field:NotBlank @Column("name") val name: String,
    @field:NotNull @field:Positive @Column("price") val price: Float,
    @Column("likes") val likes: Int,
    @field:JsonIgnore @Column("imageUrl") val path: String? = null,
    @field:NotBlank @Column("description") val description: String,
    @field:NotNull @Column("categoryId") val categoryId: Int,
    @field:NotNull @Column("companyId") val companyId: UUID?,
) {

    fun getImageUrl(): String {
        return "https://storage.googleapis.com/bucket-company-storage/$path"
    }


    constructor() : this(
        uid = null,
        discount = null,
        state = State.Available,
        name = "",
        price = -1f,
        likes = 0,
        description = "",
        categoryId = -1, companyId = null
    )

    enum class State {
        Available,
        Unavailable
    }

    @Table("UserProductLike")
    data class Like(
        @field:NotBlank @Column("userId") val userId: String,
        @field:NotNull @Column("productId") val productId: UUID,
    ) {}

}