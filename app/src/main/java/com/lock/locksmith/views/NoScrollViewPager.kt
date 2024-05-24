package com.lock.locksmith.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author lipeilin
 * @date 2024/5/23
 * @desc
 */
class NoScrollViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs) {
    private val isScrollable = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (!isScrollable) {
            false
        } else {
            super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (!isScrollable) {
            false
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }
}