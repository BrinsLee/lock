package com.lock.locksmith.extensions.binding

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.lock.locksmith.adapter.RecyclerViewAdapter
import com.lock.locksmith.adapter.RecyclerViewAdapter2
import com.lock.locksmith.extensions.combineWith
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.viewmodel.AddItemViewModel

/**
 * @author lipeilin
 * @date 2024/5/18
 * @desc
 */
var isLoaded = false

public fun AddItemViewModel.bindView(
    view: RecyclerView,
    adapter: RecyclerViewAdapter,
    helper: QuickAdapterHelper,
    lifecycleOwner: LifecycleOwner,
) {
    baseDataListState.combineWith(paginationState) { state, paginationState ->
        paginationState?.let {
            helper.trailingLoadState = LoadState.NotLoading(it.endOfChannels && it.loadingMore)

        }
        var list: List<BaseData> = state?.dataList ?: emptyList()
        list to (paginationState?.loadingMore == true)
    }.distinctUntilChanged().observe(lifecycleOwner) { (list, loadingMore) ->
        when {
             list.isEmpty() -> {
                (adapter).addAll(list)
            }
            else -> {
                if (loadingMore) {
                    // 非第一页
                    (adapter).addAll(list)
                } else {
                    // 第一页
                    (adapter).submitList(list)
                }
            }
        }
    }
}

public fun AddItemViewModel.bindView2(
    view: RecyclerView,
    adapter: RecyclerViewAdapter2,
    lifecycleOwner: LifecycleOwner,
) {
    baseDataListState.combineWith(paginationState) { state, paginationState ->
        /*paginationState?.let {
            helper.trailingLoadState = LoadState.NotLoading(it.endOfChannels && it.loadingMore)

        }*/
        var list: List<BaseData> = state?.dataList ?: emptyList()
        list to (paginationState?.loadingMore == true)
    }.distinctUntilChanged().observe(lifecycleOwner) { (list, loadingMore) ->
        when {
            list.isEmpty() -> {
                // (adapter).addAll(list)
            }
            else -> {
                if (loadingMore) {
                    // 非第一页
                    (adapter).setData(list)
                } else {
                    // 第一页
                    (adapter).setData(list)
                }
            }
        }
    }
}