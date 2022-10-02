package com.amade.dev.shoppingapp.pagination

interface PageMarkup {
    suspend fun total(): Long
}