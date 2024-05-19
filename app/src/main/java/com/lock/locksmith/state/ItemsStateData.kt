package com.lock.locksmith.state

import com.lock.locksmith.model.base.BaseData

/**
 * @author lipeilin
 * @date 2024/5/7
 * @desc
 */
sealed class ItemsStateData {

    /** No query is currently running.
     * If you know that a query will be started you typically want to display a loading icon.
     */
    public object NoQueryActive : ItemsStateData() {
        override fun toString(): String = "ItemsStateData.NoQueryActive"
    }


    /** Indicates we are loading the first page of results.
     * We are in this state if QueryChannelsState.loading is true
     * For seeing if we're loading more results have a look at QueryChannelsState.loadingMore
     *
     * @see QueryItemsState.loadingMore
     * @see QueryItemsState.loading
     */
    public object Loading : ItemsStateData() {
        override fun toString(): String = "ItemsStateData.Loading"
    }


    /** If we don't have items stored in offline storage, typically displayed as an error condition. */
    public object NoResults : ItemsStateData() {
        override fun toString(): String = "ItemsStateData.NoResults"
    }

    /** The list of items, loaded either from offline storage.
     *
     */
    public data class Result(val items: List<BaseData>) : ItemsStateData() {
        override fun toString(): String {
            return "ItemsStateData.Result(items.size=${items.size})"
        }
    }
}