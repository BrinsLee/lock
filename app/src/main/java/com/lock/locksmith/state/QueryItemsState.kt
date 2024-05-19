package com.lock.locksmith.state

import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.filter.FilterObject
import com.lock.locksmith.model.querysort.QuerySorter
import com.lock.locksmith.model.request.QueryItemsRequest
import kotlinx.coroutines.flow.StateFlow

/**
 * @author lipeilin
 * @date 2024/5/4
 * @desc Contains a state related to a single query Items request.
 */
interface QueryItemsState {

    /** If the item need to be synced. */
    public val recoveryNeeded: StateFlow<Boolean>

    /** The filter is associated with this query items state. */
    public val filter: FilterObject

    /** The sort is associated with this query items state. */
    public val sort: QuerySorter<BaseData>

    /** The current request for current page. */
    public val currentRequest: StateFlow<QueryItemsRequest?>

    /** The next page request. */
    public val nextPageRequest: StateFlow<QueryItemsRequest?>

    /** If the current state is being loaded. */
    public val loading: StateFlow<Boolean>

    /** If the current state is loading more item (a next page is being loaded). */
    public val loadingMore: StateFlow<Boolean>

    /** If the current state reached the final page. */
    public val endOfItems: StateFlow<Boolean>

    /**
     * The collection of items loaded by current query items request.
     * The stateFlow is initialized with null which means that no items have been loaded yet.
     */
    public val items: StateFlow<List<BaseData>?>


    /** The items loaded state. See [ChannelsStateData]. */
    public val itemsStateData: StateFlow<ItemsStateData>

    companion object
}