package com.amade.dev.shoppingapp.repository.menu

import com.amade.dev.shoppingapp.model.menu.ProductOrder
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductOrderRepository : CoroutineCrudRepository<ProductOrder, UUID> {}

