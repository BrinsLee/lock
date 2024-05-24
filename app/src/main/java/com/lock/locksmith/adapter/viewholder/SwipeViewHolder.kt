package com.lock.locksmith.adapter.viewholder

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.lock.locksmith.extensions.dp
import com.lock.locksmith.views.ItemListView
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
abstract class SwipeViewHolder(itemView: View) : BaseItemListItemViewHolder(itemView) {

    /**
     * The view that will be swiped.
     */
    public abstract fun getSwipeView(): View

    /**
     * The X position where the swipe view is considered to be opened.
     */
    public abstract fun getOpenedX(): Float

    /**
     * The X position where the swipe view is considered to be closed.
     */
    public abstract fun getClosedX(): Float

    /**
     * The range which the swipe view can slide
     */
    public abstract fun getSwipeDeltaRange(): ClosedFloatingPointRange<Float>

    /**
     * If swipe is enabled or disabled
     */
    public abstract fun isSwipeEnabled(): Boolean

    /**
     * If the swipe view is swiped of not. When true, swipe view is completely swiped, when false it is in the default state
     */
    public abstract fun isSwiped(): Boolean

    protected var listener: ItemListView.SwipeListener? = null
    protected var swiping: Boolean = false

    /**
     * Set the swipe listener
     */
    @SuppressLint("ClickableViewAccessibility")
    public fun setSwipeListener(view: View, swipeListener: ItemListView.SwipeListener?) {
        var startX = 0f
        var startY = 0f
        var prevX = 0f
        var wasSwiping = false
        listener = swipeListener

        view.setOnTouchListener { _, event ->
            if (!isSwipeEnabled()) {
                return@setOnTouchListener false
            }
            val rawX = event.rawX
            val rawY = event.rawY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = rawX
                    startY = rawY
                    prevX = rawX
                    wasSwiping = false
                    // don't know if it's a swipe yet; assume it's not
                    swiping = false
                    // don't consume
                    swiping
                }

                MotionEvent.ACTION_MOVE -> {
                    // calculate the total delta for both axes
                    val totalDeltaX = rawX - startX
                    val totalDeltaY = rawY - startY
                    // calculate the delta from the last event to this one
                    val lastMoveDeltaX = rawX - prevX
                    // now that we've calculated, update the previous x value with this event's x
                    prevX = rawX
                    // store the old swiping value so we can determine if we were ever swiping
                    wasSwiping = swiping
                    // determine if it's a swipe by comparing total axis delta magnitude
                    swiping = totalDeltaX.absoluteValue > totalDeltaY.absoluteValue

                    when {
                        // we've started swiping
                        !wasSwiping && swiping -> {
                            swipeListener?.onSwipeStarted(this, absoluteAdapterPosition, rawX, rawY)
                        }
                        // signal swipe movement
                        swiping -> {
                            swipeListener?.onSwipeChanged(
                                this,
                                absoluteAdapterPosition,
                                lastMoveDeltaX,
                                totalDeltaX
                            )
                        }
                        // axis magnitude measurement has dictated we are no longer swiping
                        wasSwiping && !swiping -> {
                            swipeListener?.onSwipeCanceled(
                                this,
                                absoluteAdapterPosition,
                                rawX,
                                rawY
                            )
                        }
                    }
                    swiping
                }

                MotionEvent.ACTION_UP -> {
                    // we should consume if we were swiping
                    var shouldConsume = false
                    if (wasSwiping) {
                        // no longer swiping
                        swiping = false
                        wasSwiping = false
                        // we should consume if we were swiping, and past threshold
                        shouldConsume = abs(rawX - startX) > SWIPE_THRESHOLD
                        // signal end of swipe
                        swipeListener?.onSwipeCompleted(this, absoluteAdapterPosition, rawX, rawY)
                    }
                    // consume if swipe distance is bigger than threshold
                    shouldConsume
                }

                MotionEvent.ACTION_CANCEL -> {
                    // take action if we were swiping, otherwise leave it alone
                    if (wasSwiping) {
                        // no longer swiping...
                        swiping = false
                        wasSwiping = false
                        // signal cancellation
                        swipeListener?.onSwipeCanceled(this, absoluteAdapterPosition, rawX, rawY)
                    }

                    wasSwiping
                }

                else -> false

            }
        }
    }

    private companion object {
        private val SWIPE_THRESHOLD = 16.dp
    }
}