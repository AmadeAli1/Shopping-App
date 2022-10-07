package com.amade.dev.shoppingapp.model.menu

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotBlank

@Table("Category")
data class Category(
    @Id
    @Column("id") val id: Int? = null,
    @field:NotBlank @Column("name") val name: String,
    @field:NotBlank @Column("description") val description: String,
    @field:JsonIgnore @Column("imageUrl") var path: String?,
    @Column("total") var totalProducts: Int = 0,
) {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    fun getImageUrl(): String {
        return "https://storage.googleapis.com/bucket-company-storage/$path"
    }


    constructor() : this(
        id = null, name = "", description =
        "", path = ""
    )

}
