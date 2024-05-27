package com.lock.locksmith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.lock.locksmith.bean.ItemOptionData
import com.lock.locksmith.extensions.addFlow
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.base.BaseDataListState
import com.lock.locksmith.model.filter.FilterObject
import com.lock.locksmith.model.filter.Filters
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.model.querysort.QuerySortByField
import com.lock.locksmith.model.querysort.QuerySorter
import com.lock.locksmith.model.request.QueryItemsRequest
import com.lock.locksmith.repository.additem.IAddItemRepository
import com.lock.locksmith.state.ItemsStateData
import com.lock.locksmith.state.QueryItemsState
import com.lock.locksmith.viewmodel.AddItemViewModel.AddItemEvent.AddPasswordEvent
import com.lock.locksmith.viewmodel.AddItemViewModel.AddItemEvent.AddSecureNoteEvent
import com.lock.result.onErrorSuspend
import com.lock.result.onSuccessSuspend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
@HiltViewModel
class AddItemViewModel @Inject constructor(private val repository: IAddItemRepository) :
    BaseViewModel() {

    private var queryJob: Job? = null

    /**
     * 选项数据
     */
    private val _itemOptionData = MutableLiveData<ItemOptionData>()
    var itemOptionData: LiveData<ItemOptionData> = _itemOptionData

    /**
     * 代表当前查询状态
     */
    private var queryItemsState: StateFlow<QueryItemsState?> = MutableStateFlow(null)

    /**
     * 列表数据
     */
    private val _baseDataListStateMerger: MediatorLiveData<BaseDataListState> =
        MediatorLiveData()
    public val baseDataListState: LiveData<BaseDataListState> =
        _baseDataListStateMerger.distinctUntilChanged()

    /**
     * 表示当前分页状态，该状态是多个源的产物。
     *
     */
    private val paginationStateMerger = MediatorLiveData<PaginationState>()

    /**
     * 通过包含有关加载状态的信息以及是否已到达所有可用item的末端来表示当前分页状态
     */
    public val paginationState: LiveData<PaginationState> = paginationStateMerger.distinctUntilChanged()


    /**
     * 过滤条件
     */
    private var filter: FilterObject? = null

    /**
     * 排序条件
     */
    private var sort: QuerySorter<BaseData> = DEFAULT_SORT

    /**
     * 每次查询条数
     */
    private val limit: Int = 5

    companion object {
        @JvmField
        public val DEFAULT_SORT: QuerySorter<BaseData> = QuerySortByField.ascByName("createDate")

        /**
         *  The initial state.
         */
        private val INITIAL_STATE: BaseDataListState = BaseDataListState(isLoading = true)
    }

    init {
        observeItemDataListState()
    }

/*    private fun observerItemDataState(): StateFlow<ItemDataState> {
        return repository.queryItemsAsState(20, viewModelScope)
    }*/

    private fun observeItemDataListState() {
        _baseDataListStateMerger.postValue(INITIAL_STATE)
        val queryItemsRequest =
            QueryItemsRequest(
                filter = buildDefaultFilter(),
                querySort = sort,
                limit = limit,
            )
        queryItemsState = repository.queryItemsAsState(queryItemsRequest, viewModelScope)
        queryJob?.cancel()
        val queryJob = Job(viewModelScope.coroutineContext.job).also {
            this.queryJob = it
        }
        viewModelScope.launch(queryJob) {
            queryItemsState.filterNotNull().collectLatest { queryItemsState ->
                if (!isActive) {
                    return@collectLatest
                }
                _baseDataListStateMerger.addFlow(
                    queryJob,
                    queryItemsState.itemsStateData
                ) { itemsState ->
                    _baseDataListStateMerger.value = handleItemStateNew(itemsState)
                }
                paginationStateMerger.addFlow(queryJob, queryItemsState.loadingMore) { loadingMore ->
                    setPaginationState { copy(loadingMore = loadingMore) }
                }
                paginationStateMerger.addFlow(queryJob, queryItemsState.endOfItems) { endOfItems ->
                    setPaginationState { copy(endOfItems = endOfItems) }
                }
            }
        }

        /*combine(itemDataState) {
            val state: ItemDataState = it[0]
            when (state) {
                is ItemDataState.IDEL, ItemDataState.Loading -> {
                    _itemDataListState.value.copy(isLoading = true)
                }

                is ItemDataState.Result -> {
                    _itemDataListState.value.copy(dataList = state.dataList, isLoading = false)
                }

                is ItemDataState.Failure -> {
                    _itemDataListState.value.copy(isLoading = false)
                }
            }
        }.distinctUntilChanged().catch {

        }.launchIn(scope)*/
    }

    private fun handleItemStateNew(itemsState: ItemsStateData): BaseDataListState {
        return when (itemsState) {
            is ItemsStateData.NoQueryActive, ItemsStateData.Loading -> BaseDataListState(isLoading = true, isLoadingMore = false)
            is ItemsStateData.NoResults -> BaseDataListState(isLoading = false, isLoadingMore = false)
            is ItemsStateData.Result -> BaseDataListState(isLoading = false, isLoadingMore = false, dataList = itemsState.items)
        }
    }

    fun fetchItemOptionData() {
        repository.getItemData().onSuccess {
            _itemOptionData.postValue(it)
        }
    }

    public fun onAction(action: Action) {
        when (action) {
            is Action.ReachedEndOfList -> requestMoreItems()
        }
    }

    private fun requestMoreItems() {
        val queryItemsState = queryItemsState.value ?: return
        queryItemsState.nextPageRequest.value?.let {
            repository.queryItemsAsState(it, viewModelScope)

        }
    }

    fun handleEvent(event: AddItemEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(State.Loading)
            when (event) {
                is AddPasswordEvent -> {
                    repository.saveItemData(event.itemData).onSuccessSuspend {
                        _state.emit(State.Result(""))
                    }.onErrorSuspend {
                        _state.emit((State.Failure(it.message)))
                    }
                }

                is AddSecureNoteEvent -> TODO()
            }
        }
    }

    public sealed class AddItemEvent(open val itemData: BaseData) {

        public class AddPasswordEvent(override val itemData: PasswordData) : AddItemEvent(itemData)

        public class AddSecureNoteEvent(override val itemData: BaseData) : AddItemEvent(itemData)
    }

    /*    public sealed class ItemDataState {
            public data object IDEL : ItemDataState() {
                override fun toString(): String {
                    return "IDEL"
                }
            }

            public data object Loading : ItemDataState() {
                override fun toString(): String {
                    return "Loading"
                }
            }

            public data class Result(val dataList: List<BaseData>) : ItemDataState()

            public data class Failure(val error: String) : ItemDataState() {
                override fun toString(): String {
                    return "Failure"
                }
            }
        }*/
    private fun setPaginationState(reducer: PaginationState.() -> PaginationState) {
        paginationStateMerger.value = reducer(paginationStateMerger.value ?: PaginationState())
    }

    public data class PaginationState(
        val loadingMore: Boolean = false,
        val endOfItems: Boolean = false,
    )

    private fun buildDefaultFilter(): FilterObject {
        return Filters.and(
            Filters.eq("type", "messaging"),
        )
    }


    /**
     * Describes the available actions that can be taken.
     */
    public sealed class Action {
        public object ReachedEndOfList : Action() {
            override fun toString(): String = "ReachedEndOfList"
        }
    }
}