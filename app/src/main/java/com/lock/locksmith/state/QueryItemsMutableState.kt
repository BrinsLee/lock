package com.lock.locksmith.state

import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.filter.FilterObject
import com.lock.locksmith.model.querysort.QuerySorter
import com.lock.locksmith.model.request.QueryItemsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * @author lipeilin
 * @date 2024/5/7
 * @desc
 */
class QueryItemsMutableState(
    override val filter: FilterObject,
    override val sort: QuerySorter<BaseData>,
    scope: CoroutineScope
) : QueryItemsState {

    private var _items: MutableStateFlow<Map<String, BaseData>?>? = MutableStateFlow(null)
    private val mapItems: StateFlow<Map<String, BaseData>?> = _items!!

    private var _loading: MutableStateFlow<Boolean>? = MutableStateFlow(false)
    private var _loadingMore: MutableStateFlow<Boolean>? = MutableStateFlow(false)

    internal val currentLoading: StateFlow<Boolean>
        get() = if (items.value.isNullOrEmpty()) loading else loadingMore

    fun isLoading(): Boolean = currentLoading.value

    private var _endOfItems: MutableStateFlow<Boolean>? = MutableStateFlow(false)

    private val sortedItems: StateFlow<List<BaseData>?> =
        mapItems.map { items ->
            items?.values?.sortedWith(sort.comparator)
        }.stateIn(scope, SharingStarted.Eagerly, null)

    private var _currentRequest: MutableStateFlow<QueryItemsRequest?>? = MutableStateFlow(null)
    private var _recoveryNeeded: MutableStateFlow<Boolean>? = MutableStateFlow(false)
    private var _itemsOffset: MutableStateFlow<Int>? = MutableStateFlow(0)
    internal val itemsOffset: StateFlow<Int> = _itemsOffset!!

    override val recoveryNeeded: StateFlow<Boolean> = _recoveryNeeded!!
    override val currentRequest: StateFlow<QueryItemsRequest?> = _currentRequest!!
    override val nextPageRequest: StateFlow<QueryItemsRequest?> =
        currentRequest.combine(itemsOffset) { request, offset ->
            request?.copy(offset = offset)
        }.stateIn(scope, SharingStarted.Eagerly, null)

    override val loading: StateFlow<Boolean> = _loading!!

    override val loadingMore: StateFlow<Boolean> = _loadingMore!!

    override val endOfItems: StateFlow<Boolean> = _endOfItems!!

    override val items: StateFlow<List<BaseData>?> = sortedItems

    override val itemsStateData: StateFlow<ItemsStateData> =
        loading.combine(sortedItems) { loading: Boolean, items: List<BaseData>? ->
            when {
                loading || items == null -> ItemsStateData.Loading
                items.isEmpty() -> ItemsStateData.NoResults
                else -> ItemsStateData.Result(items)
            }
        }.stateIn(scope, Companion.Eagerly, ItemsStateData.NoQueryActive)



    fun setLoadingMore(isLoading: Boolean) {
        _loadingMore?.value = isLoading
    }

    /**
     * Set loading more. Notifies if the SDK is loading the first page.
     */
    fun setLoadingFirstPage(isLoading: Boolean) {
        _loading?.value = isLoading
    }

    /**
     * Set the current request being made.
     *
     * @param request [QueryChannelsRequest]
     */
    fun setCurrentRequest(request: QueryItemsRequest) {
        _currentRequest?.value = request
    }

    /**
     * Set the end of channels.
     *
     * @parami isEnd Boolean
     */
    fun setEndOfItems(isEnd: Boolean) {
        _endOfItems?.value = isEnd
    }

    /**
     * Sets if recovery is needed.
     *
     * @param recoveryNeeded Boolean
     */
    fun setRecoveryNeeded(recoveryNeeded: Boolean) {
        _recoveryNeeded?.value = recoveryNeeded
    }

    /**
     * Set the offset of the channels.
     *
     * @param offset Int
     */
    fun setItemsOffset(offset: Int) {
        _itemsOffset?.value = offset
    }

    var rawItems: Map<String, BaseData>?
        get() = _items?.value
        private set(value) {
            _items?.value = value
        }

    fun setItems(itemsMap: Map<String, BaseData>) {
        rawItems = itemsMap
    }

    fun destroy() {
        _items = null
        _loading = null
        _loadingMore = null
        _endOfItems = null
        _currentRequest = null
        _recoveryNeeded = null
        _itemsOffset = null
    }
}

fun QueryItemsState.toMutableState(): QueryItemsMutableState = this as QueryItemsMutableState