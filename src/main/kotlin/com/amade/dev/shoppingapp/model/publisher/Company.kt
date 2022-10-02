package com.amade.dev.shoppingapp.model.publisher

import com.amade.dev.shoppingapp.model.user.City
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Table("Company")
data class Company(
    @Id @Column("uid") val uid: UUID? = null,
    @field:Email @field:NotBlank @Column("email") val email: String,
    @Column("logoUrl") val logoUrl: String? = null,
    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY) @field:NotBlank @Length(min = 6) @Column("password") val password: String,
    @Column("subscribers") val subscribers: Int,
    @field:Size(min = 1) @Column("phoneNumbers") val phoneNumbers: Array<String>,
    @field:NotBlank @Column("description") val description: String,
    @field:NotBlank @Column("name") val companyName: String,
    @field:NotBlank @Column("locationName") val locationName: String,
    @field:NotNull @Column("city") val city: City,
) {


    constructor() : this(
        uid = null,
        email = "",
        logoUrl = null,
        password = "",
        subscribers = 0,
        phoneNumbers = emptyArray(),
        description = "",
        companyName = "",
        locationName = "",
        city = City.Maputo
    )

    @JsonIgnore
    fun getPath(): String {
        return "company/$uid/${logoUrl!!.substringAfterLast("/")}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Company

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid?.hashCode() ?: 0
    }

}
