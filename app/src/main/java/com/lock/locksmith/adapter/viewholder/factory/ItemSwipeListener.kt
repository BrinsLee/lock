package com.lock.locksmith.adapter.viewholder.factory

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.lock.locksmith.adapter.viewholder.ItemViewHolder
import com.lock.locksmith.adapter.viewholder.SwipeViewHolder
import com.lock.locksmith.extensions.safeCast
import com.lock.locksmith.views.ItemListView
import com.lock.locksmith.views.layoutmanager.ScrollPauseLinearLayoutManager

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class ItemSwipeListener @JvmOverloads constructor(
    val recyclerView: RecyclerView,
    val layoutManager: ScrollPauseLinearLayoutManager,
    private val swipeStateByPosition: MutableMap<Int, SwipeState> = mutableMapOf(),
    private var multiSwipeEnabled: Boolean = false
) : ItemListView.SwipeListener {

    sealed class SwipeState {
        internal object Open : SwipeState() {
            override fun toString(): String = "Open"
        }

        internal object Closed : SwipeState() {
            override fun toString(): String = "Closed"
        }
    }

    override fun onSwipeStarted(
        viewHolder: SwipeViewHolder,
        adapterPosition: Int,
        x: Float?,
        y: Float?
    ) {
        // disable scrolling
        layoutManager.verticalScrollEnabled = false
    }

    override fun onSwipeChanged(
        viewHolder: SwipeViewHolder,
        adapterPosition: Int,
        dX: Float,
        totalDeltaX: Float
    ) {
        // our edge starts at 0, so our x can always be clamped into our delta range
        val projectedX = viewHolder.getSwipeView().x + dX
        // clamp it and animate if necessary
        projectedX.coerceIn(viewHolder.getSwipeDeltaRange()).let { clampedX ->
            // set the new x if it's different
            val swipeView = viewHolder.getSwipeView()
            if (swipeView.x != clampedX) {
                swipeView.x = clampedX
            }
        }
        // cancel ripple animation
        viewHolder.itemView.isPressed = false
    }

    /**
     * Called when swipe is completed.
     *
     * @param viewHolder [SwipeViewHolder].
     * @param adapterPosition
     * @param x position in the X axis.
     * @param y position in the Y axis.
     */
    override fun onSwipeCompleted(
        viewHolder: SwipeViewHolder,
        adapterPosition: Int,
        x: Float?,
        y: Float?
    ) {

        // determine snap value
        val snapValue = if (viewHolder.isSwiped()) {
            viewHolder.getOpenedX()
        } else {
            viewHolder.getClosedX()
        }

        // animate to snap
        viewHolder.getSwipeView().animateSwipeTo(snapValue)

        val swipeState = if (viewHolder.isSwiped()) {
            SwipeState.Open
        } else {
            SwipeState.Closed
        }
        // persist swipe state for the current item
        swipeStateByPosition[adapterPosition] = swipeState
        if (!multiSwipeEnabled && swipeState == SwipeState.Open) {
            closeAllOtherPositions(adapterPosition)
        }
        // re-enable scrolling
        layoutManager.verticalScrollEnabled = true
    }

    override fun onSwipeCanceled(
        viewHolder: SwipeViewHolder,
        adapterPosition: Int,
        x: Float?,
        y: Float?
    ) {
        // animate back to closed position
        viewHolder.getSwipeView().animateSwipeTo(viewHolder.getClosedX())
        // persist swipe state
        swipeStateByPosition[adapterPosition] = SwipeState.Closed
        // re-enable scrolling
        layoutManager.verticalScrollEnabled = true
    }

    override fun onRestoreSwipePosition(viewHolder: SwipeViewHolder, adapterPosition: Int) {
        viewHolder.apply {
            getSwipeView().x = when (swipeStateByPosition[adapterPosition]) {
                SwipeState.Open -> getOpenedX()
                else -> getClosedX()
            }
        }
    }

    private fun closeAllOtherPositions(adapterPosition: Int) {
        swipeStateByPosition.asSequence()
            .filter { it.key != adapterPosition }
            .filter { it.value == SwipeState.Open }
            .forEach { swipeStateEntry ->
                swipeStateByPosition[swipeStateEntry.key] = SwipeState.Closed
                // if the view holder currently visible, animate it closed

                recyclerView.findViewHolderForAdapterPosition(swipeStateEntry.key)
                    ?.safeCast<ItemViewHolder>()?.let { viewHolder ->
                        val viewCompletelyVisible =
                            layoutManager.isViewPartiallyVisible(viewHolder.itemView, true, false)
                        val viewPartiallyVisible =
                            layoutManager.isViewPartiallyVisible(viewHolder.itemView, false, false)
                        val onScreen = viewCompletelyVisible || viewPartiallyVisible
                        if (onScreen) {
                            viewHolder.getSwipeView().animateSwipeTo(viewHolder.getClosedX())
                        }
                    }

            }
    }

    private fun View.animateSwipeTo(value: Float) {
        animate()
            .x(value)
            .setStartDelay(0)
            .setDuration(100)
            .start()
    }
}