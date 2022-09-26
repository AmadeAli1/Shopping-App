package com.amade.dev.shoppingapp.repository.publisher

import com.amade.dev.shoppingapp.model.publisher.CompanyAddress
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyAddressRepository : CoroutineCrudRepository<CompanyAddress, Int> {}