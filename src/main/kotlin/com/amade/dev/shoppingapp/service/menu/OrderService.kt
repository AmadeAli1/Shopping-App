package com.amade.dev.shoppingapp.service.menu

import com.amade.dev.shoppingapp.repository.menu.ProductOrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: ProductOrderRepository,
)