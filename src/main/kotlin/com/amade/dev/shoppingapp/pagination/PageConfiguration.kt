package com.amade.dev.shoppingapp.pagination

class PageConfiguration<T> {

    suspend fun config(
        repository: PageMarkup,
        page: Int,
        onComplete: suspend (total: Long, pages: Int, start: Int) -> Page<T>,
    ): Page<T> {
        val limit = 20.0
        val totalItems = repository.total()
        val pages = totalItems.div(limit).plus(1).toInt()
        val start = if (page == 1) {
            0
        } else {
            (page - 1) * limit.toInt()
        }
        return onComplete(totalItems, pages, start)
    }

    suspend fun config(
        total: Long,
        page: Int,
        onComplete: suspend (total: Long, pages: Int, start: Int) -> Page<T>,
    ): Page<T> {
        val limit = 20.0
        val pages = total.div(limit).plus(1).toInt()
        val start = if (page == 1) {
            0
        } else {
            (page - 1) * limit.toInt()
        }
        return onComplete(total, pages, start)
    }


    fun getPage(data: List<T>, pages: Int, totalItems: Long, page: Int, hasNext: Boolean): Page<T> {
        return Page<T>(
            data = data,
            pageSize = data.size,
            pageNumber = page,
            totalPages = pages,
            totalItems = totalItems,
            maxPageSize = 20,
            nextPage = if (!hasNext) null else page + 1,
            prevPage = if (page == 1) null else page - 1
        )
    }


}