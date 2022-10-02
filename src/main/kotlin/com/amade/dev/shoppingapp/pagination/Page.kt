package com.amade.dev.shoppingapp.pagination

data class Page<T>(
    val data: List<T>,
    val pageSize: Int,
    val pageNumber: Int,
    val totalPages: Int,
    val totalItems: Long,
    val maxPageSize: Int,
    val nextPage: Int?,
    val prevPage: Int?
)
