package com.lock.locksmith.extensions.binding

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.distinctUntilChanged
import com.lock.locksmith.adapter.listitem.ItemListItem
import com.lock.locksmith.extensions.combineWith
import com.lock.locksmith.viewmodel.AddItemViewModel
import com.lock.locksmith.views.ItemListView

/**
 * @author lipeilin
 * @date 2024/5/18
 * @desc
 */

public fun AddItemViewModel.bindView(
    view: ItemListView,
    lifecycleOwner: LifecycleOwner,
) {
    baseDataListState.combineWith(paginationState) { state, paginationState ->
        paginationState?.let {
            view.setPaginationEnabled(!it.endOfItems && !it.loadingMore)
            /*val loading = state?.isLoading ?: false
            val endOfItems = it.endOfItems
            val loadingMore = it.loadingMore
            if (loading) {
                Log.d("bindView", "loading loading $loading, endOfPage $endOfItems, loadingMore $loadingMore")
            } else if (loadingMore) {
                Log.d("bindView", "loadingMore loading $loading, endOfPage $endOfItems, loadingMore $loadingMore")
            } else {
                // 非加载，非加载更多
                Log.d("bindView", "none loading $loading, endOfPage $endOfItems, loadingMore $loadingMore")

            }*/
        }
        var list: List<ItemListItem> = state?.dataList?.map { ItemListItem.NormalItem(it) } ?: emptyList()
        if (paginationState?.loadingMore == true) {
            list = list + ItemListItem.LoadingMoreItem
        }
        list to (state?.isLoading == true)
    }.distinctUntilChanged().observe(lifecycleOwner) { (list, isLoading) ->
        when {
            isLoading && list.isEmpty() -> {
                view.showLoadingView()
            }

            list.isNotEmpty() -> {
                view.hideLoadingView()
                view.setItems(list)
            }

            else -> {
                view.hideLoadingView()
                view.setItems(emptyList())
            }
        }
        /*if (loadingMore) {
            helper.trailingLoadState = LoadState.NotLoading(endOfPage)

        } else {
            helper.trailingLoadState = LoadState.None
        }*/
    }
}

