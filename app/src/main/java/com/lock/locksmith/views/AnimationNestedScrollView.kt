package com.lock.locksmith.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * @author lipeilin
 * @date 2024/4/22
 * @desc
 */
class AnimationNestedScrollView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    def: Int = 0
) : NestedScrollView(context, attr, def) {

    var listener: OnAnimationScrollChangeListener? = null

    interface OnAnimationScrollChangeListener {
        fun onScrollChanged(dy: Float)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        listener?.onScrollChanged(scrollY * 0.65f)
    }
}