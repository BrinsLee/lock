package com.lock.locksmith.model.request

import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.filter.FilterObject
import com.lock.locksmith.model.querysort.QuerySortByField
import com.lock.locksmith.model.querysort.QuerySorter

/**
 * @author lipeilin
 * @date 2024/5/7
 * @desc request body class for querying items.
 * @param filter [FilterObject] conditions used to filter queries
 * @param offset offset for pagination
 * @param limit Number of items to return
 * @param querySort [QuerySorter] used to sort the results
 */
data class QueryItemsRequest(val filter: FilterObject,
    var offset: Int = 0,
    var limit: Int = 20,
    val querySort: QuerySorter<BaseData> = QuerySortByField(),
    ) {

    /**
     * List of sort specifications.
     */
    public val sort: List<Map<String, Any>> = querySort.toDto()

    /**
     * Sets the number of item to be returned by this backend.
     *
     * @param limit Number of channels to limit.
     *
     * @return [QueryItemsRequest] with updated limit.
     */
    public fun withLimit(limit: Int): QueryItemsRequest {
        this.limit = limit
        return this
    }


    /**
     * Sets the offset to this request.
     *
     * @param offset The offset value to set.
     *
     * @return [QueryItemsRequest] with updated offset.
     */
    public fun withOffset(offset: Int): QueryItemsRequest {
        this.offset = offset
        return this
    }

    /**
     * True if this request is querying the first page, otherwise False.
     */
    public val isFirstPage: Boolean
        get() = offset == 0
}
