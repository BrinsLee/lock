package com.lock.locksmith.adapter.viewholder.factory

import com.lock.locksmith.adapter.viewholder.SwipeViewHolder
import com.lock.locksmith.utils.ListenerDelegate
import com.lock.locksmith.views.ItemListView
import com.lock.locksmith.views.ItemListView.ItemClickListener
import com.lock.locksmith.views.ItemListView.ItemLongClickListener
import com.lock.locksmith.views.ItemListView.SwipeListener

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class ItemListListenerContainerImpl(
    itemClickListener: ItemClickListener = ItemClickListener.DEFAULT,
    itemLongClickListener: ItemListView.ItemLongClickListener = ItemListView.ItemLongClickListener.DEFAULT,
    copyClickListener: ItemClickListener = ItemClickListener.DEFAULT,
    deleteClickListener: ItemClickListener = ItemClickListener.DEFAULT,
    swipeListener: ItemListView.SwipeListener = ItemListView.SwipeListener.DEFAULT,
): ItemListListenerContainer {
    override var itemClickListener: ItemClickListener by ListenerDelegate<ItemClickListener>(itemClickListener) { realListener ->
        ItemClickListener {
            realListener().onClick(it)
        }
    }
    override var itemLongClickListener: ItemLongClickListener by ListenerDelegate<ItemLongClickListener> (itemLongClickListener) { realListener ->
        ItemLongClickListener {
            realListener().onLongClick(it)
        }
    }
    override var deleteClickListener: ItemClickListener by ListenerDelegate<ItemClickListener>(deleteClickListener) { realListener ->
        ItemClickListener {
            realListener().onClick(it)
        }
    }
    override var copyClickListener: ItemClickListener by ListenerDelegate<ItemClickListener>(copyClickListener) { realListener ->
        ItemClickListener {
            realListener().onClick(it)
        }
    }
    override var swipeListener: SwipeListener by ListenerDelegate<SwipeListener>(swipeListener) { realListener ->
        object : SwipeListener {
            override fun onSwipeStarted(
                viewHolder: SwipeViewHolder,
                adapterPosition: Int,
                x: Float?,
                y: Float?
            ) {
                realListener().onSwipeStarted(viewHolder, adapterPosition, x, y)
            }

            override fun onSwipeChanged(
                viewHolder: SwipeViewHolder,
                adapterPosition: Int,
                dX: Float,
                totalDeltaX: Float
            ) {
                realListener().onSwipeChanged(viewHolder, adapterPosition, dX, totalDeltaX)
            }

            override fun onSwipeCompleted(
                viewHolder: SwipeViewHolder,
                adapterPosition: Int,
                x: Float?,
                y: Float?
            ) {
                realListener().onSwipeCompleted(viewHolder, adapterPosition, x, y)
            }

            override fun onSwipeCanceled(
                viewHolder: SwipeViewHolder,
                adapterPosition: Int,
                x: Float?,
                y: Float?
            ) {
                realListener().onSwipeCanceled(viewHolder, adapterPosition, x, y)
            }

            override fun onRestoreSwipePosition(viewHolder: SwipeViewHolder, adapterPosition: Int) {
                realListener().onRestoreSwipePosition(viewHolder, adapterPosition)
            }
        }
    }
}