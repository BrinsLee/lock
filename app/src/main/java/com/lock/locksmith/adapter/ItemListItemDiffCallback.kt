package com.lock.locksmith.adapter

import androidx.recyclerview.widget.DiffUtil
import com.lock.locksmith.adapter.listitem.ItemListItem
import com.lock.locksmith.adapter.playload.ItemListPayloadDiff
import com.lock.locksmith.extensions.cast
import com.lock.locksmith.extensions.safeCast
import com.lock.locksmith.model.base.BaseData

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
object ItemListItemDiffCallback : DiffUtil.ItemCallback<ItemListItem>(){
    override fun areItemsTheSame(oldItem: ItemListItem, newItem: ItemListItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return when (oldItem) {
            is ItemListItem.NormalItem -> {
                oldItem.item.getMetaAccountId()
                    .contentEquals(newItem.safeCast<ItemListItem.NormalItem>()?.item?.getMetaAccountId())
            }

            else -> true
        }
    }

    override fun areContentsTheSame(oldItem: ItemListItem, newItem: ItemListItem): Boolean {
        return when (oldItem) {
            is ItemListItem.NormalItem -> {
                oldItem
                    .diff(newItem.cast())
                    .hasDifference()
                    .not()
            }

            else -> true
        }
    }

    override fun getChangePayload(oldItem: ItemListItem, newItem: ItemListItem): Any? {
        return oldItem
            .cast<ItemListItem.NormalItem>()
            .diff(newItem.cast())
    }


    private fun ItemListItem.NormalItem.diff(other: ItemListItem.NormalItem): ItemListPayloadDiff {
        return ItemListPayloadDiff(
            nameChanged = item.itemName != other.item.itemName,
            accountNameChanged = item.accountName != other.item.accountName,
            updateDateChanged = false
        )
    }
}