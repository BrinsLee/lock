package com.lock.locksmith.model.pagination

import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.querysort.QuerySortByField
import com.lock.locksmith.model.querysort.QuerySorter

/**
 * @author lipeilin
 * @date 2024/5/7
 * @desc
 */

private const val ITEM_LIMIT = 20

class ItemPaginationRequest {

    var sort: QuerySorter<BaseData> = QuerySortByField()

    var itemLimit: Int = ITEM_LIMIT
    var itemOffset: Int = 0

    val isFirstPage: Boolean
        get() = itemOffset == 0
}