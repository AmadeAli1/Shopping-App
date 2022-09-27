package com.amade.dev.shoppingapp.model.publisher.views

import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.model.publisher.CompanyAddress
import com.amade.dev.shoppingapp.model.publisher.dto.CompanyDTO
import com.amade.dev.shoppingapp.model.user.City
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("CompanyView")
data class CompanyView(
    @Column("uid") val uid: UUID? = null,
    @Column("password") val password: String,
    @Column("email") val email: String,
    @Column("logoUrl") val logoUrl: String? = null,
    @Column("subscribers") val subscribers: Int,
    @Column("phoneNumbers") val phoneNumbers: Array<String>,
    @Column("description") val description: String,
    @Column("name") val companyName: String,
    @Column("locationName") val locationName: String,
    @Column("city") val city: City,
    @Column("addressId") val address: Int,
    @Column("latitude") val latitude: Double,
    @Column("longitude") val longitude: Double,
) {

    fun toCompanyDTO(): CompanyDTO {
        val company = Company(
            uid,
            email,
            logoUrl,
            password,
            subscribers,
            phoneNumbers,
            description,
            companyName,
            locationName,
            city
        )

        val address = CompanyAddress(id = address, companyId = uid!!, latitude, longitude)
        return CompanyDTO(company, address)
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
