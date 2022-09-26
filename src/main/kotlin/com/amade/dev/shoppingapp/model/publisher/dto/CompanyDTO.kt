package com.amade.dev.shoppingapp.model.publisher.dto

import com.amade.dev.shoppingapp.model.publisher.Company
import com.amade.dev.shoppingapp.model.publisher.CompanyAddress
import com.amade.dev.shoppingapp.model.publisher.CompanyImage

data class CompanyDTO(
    val company: Company,
    val address: CompanyAddress? = null,
    val images: List<CompanyImage>? = null,
) {
}