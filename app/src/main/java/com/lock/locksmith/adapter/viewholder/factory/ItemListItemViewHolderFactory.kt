package com.lock.locksmith.adapter.viewholder.factory

import android.content.Context
import android.view.ViewGroup
import com.lock.locksmith.adapter.listitem.ItemListItem
import com.lock.locksmith.adapter.listitem.ItemListItemViewType
import com.lock.locksmith.adapter.viewholder.BaseItemListItemViewHolder
import com.lock.locksmith.adapter.viewholder.ItemListLoadingMoreViewHolder
import com.lock.locksmith.adapter.viewholder.ItemViewHolder
import com.lock.locksmith.views.style.ItemListViewStyle

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class ItemListItemViewHolderFactory {

    protected lateinit var listenerContainer: ItemListListenerContainer
        private set

    protected lateinit var visibilityContainer: ItemListVisibilityContainer
        private set

    protected lateinit var iconProviderContainer: ItemListIconProviderContainer
        private set

    protected lateinit var style: ItemListViewStyle
        private set

    internal fun setListenerContainer(listenerContainer: ItemListListenerContainer) {
        this.listenerContainer = listenerContainer
    }

    internal fun setVisibilityContainer(visibilityContainer: ItemListVisibilityContainer) {
        this.visibilityContainer = visibilityContainer
    }

    internal fun setIconProviderContainer(iconProviderContainer: ItemListIconProviderContainer) {
        this.iconProviderContainer = iconProviderContainer
    }

    internal fun setStyle(style: ItemListViewStyle) {
        this.style = style
    }


    public open fun getItemViewType(item: ItemListItem): Int {
        return when (item) {
            is ItemListItem.LoadingMoreItem -> ItemListItemViewType.LOADING_MORE
            is ItemListItem.NormalItem -> ItemListItemViewType.DEFAULT
        }
    }

    public open fun createViewHolder(
        parentView: ViewGroup,
        viewType: Int,
    ): BaseItemListItemViewHolder {
        return when (viewType) {
            ItemListItemViewType.DEFAULT -> createItemViewHolder(parentView)
            ItemListItemViewType.LOADING_MORE -> createLoadingMoreViewHolder(parentView)
            else -> throw IllegalArgumentException("Unhandled ItemList view type: $viewType")
        }
    }

    private fun createLoadingMoreViewHolder(parentView: ViewGroup): BaseItemListItemViewHolder {
        return ItemListLoadingMoreViewHolder(parentView, style)
    }

    protected open fun createItemViewHolder(parentView: ViewGroup): BaseItemListItemViewHolder {
        ensureInitialized(parentView.context)
        return ItemViewHolder(parentView, listenerContainer.itemClickListener, listenerContainer.itemLongClickListener, listenerContainer.copyClickListener, listenerContainer.deleteClickListener, listenerContainer.swipeListener, style, visibilityContainer.isCopyOptionsVisible, visibilityContainer.isDeleteOptionVisible, iconProviderContainer.getCopyOptionIcon, iconProviderContainer.getDeleteOptionIcon)
    }

    /**
     * Initializes the fields with the default values for testing.
     */
    private fun ensureInitialized(context: Context) {
        if (!::listenerContainer.isInitialized) {
            listenerContainer = ItemListListenerContainerImpl()
        }
        if (!::visibilityContainer.isInitialized) {
            visibilityContainer = ItemListVisibilityContainerImpl()
        }
        if (!::iconProviderContainer.isInitialized) {
            iconProviderContainer = ItemListIconProviderContainerImpl()
        }
        if (!::style.isInitialized) {
            style = ItemListViewStyle(context, null)
        }
    }
}