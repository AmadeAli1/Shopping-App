package com.amade.dev.shoppingapp.repository.publisher

import com.amade.dev.shoppingapp.model.publisher.CompanyImage
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyImageRepository : CoroutineCrudRepository<CompanyImage, Int> {
}