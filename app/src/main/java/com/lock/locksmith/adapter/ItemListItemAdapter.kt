package com.lock.locksmith.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.lock.locksmith.adapter.listitem.ItemListItem
import com.lock.locksmith.adapter.playload.ItemListPayloadDiff
import com.lock.locksmith.adapter.viewholder.BaseItemListItemViewHolder
import com.lock.locksmith.adapter.viewholder.factory.ItemListItemViewHolderFactory

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class ItemListItemAdapter(private val viewHolderFactory: ItemListItemViewHolderFactory) :
    ListAdapter<ItemListItem, BaseItemListItemViewHolder>(ItemListItemDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return viewHolderFactory.getItemViewType(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseItemListItemViewHolder {
        return viewHolderFactory.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseItemListItemViewHolder, position: Int) {
        bind(position, holder, FULL_ITEM_LIST_ITEM_PAYLOAD_DIFF)
    }

    override fun onBindViewHolder(
        holder: BaseItemListItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val diff = (payloads
            .filterIsInstance<ItemListPayloadDiff>()
            .takeIf { it.isNotEmpty() }
            ?: listOf(FULL_ITEM_LIST_ITEM_PAYLOAD_DIFF)
            ).fold(EMPTY_ITEM_LIST_ITEM_PAYLOAD_DIFF, ItemListPayloadDiff::plus)

        bind(position, holder, diff)
    }

    private fun bind(position: Int, holder: BaseItemListItemViewHolder, payload: ItemListPayloadDiff) {
        when (val channelItem = getItem(position)) {
            is ItemListItem.LoadingMoreItem -> Unit
            is ItemListItem.NormalItem -> holder.bind(channelItem, payload)
        }
    }

    companion object {
        private val FULL_ITEM_LIST_ITEM_PAYLOAD_DIFF: ItemListPayloadDiff = ItemListPayloadDiff(
            nameChanged = true,
            accountNameChanged = true,
            updateDateChanged = true,
        )

        val EMPTY_ITEM_LIST_ITEM_PAYLOAD_DIFF: ItemListPayloadDiff = ItemListPayloadDiff(
            nameChanged = false,
            accountNameChanged = true,
            updateDateChanged = true,
        )
    }
}