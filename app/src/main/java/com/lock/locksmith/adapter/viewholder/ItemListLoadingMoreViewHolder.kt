

package com.lock.locksmith.adapter.viewholder

import android.view.ViewGroup
import com.lock.locksmith.adapter.listitem.ItemListItem
import com.lock.locksmith.adapter.playload.ItemListPayloadDiff
import com.lock.locksmith.extensions.inflater
import com.lock.locksmith.views.style.ItemListViewStyle

internal class ItemListLoadingMoreViewHolder(
    parent: ViewGroup,
    style: ItemListViewStyle,
) : BaseItemListItemViewHolder(parent.inflater.inflate(style.loadingMoreView, parent, false)) {

    override fun bind(channelItem: ItemListItem.NormalItem, diff: ItemListPayloadDiff): Unit = Unit
}
