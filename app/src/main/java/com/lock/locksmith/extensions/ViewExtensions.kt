package com.lock.locksmith.extensions

import android.R
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigationrail.NavigationRailView
import com.lock.locksmith.LockSmithApplication
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.Locale
import kotlin.math.roundToInt

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */

const val ANIM_DURATION = 300L

fun View.show() {
    isVisible = true
}

fun View.hide() {
    isVisible = false
}

fun View.hidden() {
    isInvisible = true
}

fun NavigationBarView.setItemColors(@ColorInt normalColor: Int, @ColorInt selectedColor: Int) {
    val csl = ColorStateList(
        arrayOf(intArrayOf(-R.attr.state_checked), intArrayOf(R.attr.state_checked)),
        intArrayOf(normalColor, selectedColor)
    )
    itemIconTintList = csl
    itemTextColor = csl
}


fun<T> T.throttle(duration: Long = 500L): T {
    return Proxy.newProxyInstance(this!!::class.java.classLoader, this!!::class.java.interfaces, object : InvocationHandler {
        private var lastInvokeTime = System.currentTimeMillis()

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            val current = System.currentTimeMillis()
            return if (current - lastInvokeTime > duration) {
                lastInvokeTime = current;
                method.invoke(this@throttle, *args.orEmpty())
            } else {
                resolveDefaultReturnValue(method)
            }

        }
    }) as T
}

private fun resolveDefaultReturnValue(method: Method): Any? {
    return when (method.returnType.name.lowercase(Locale.US)) {
        Void::class.java.simpleName.lowercase(Locale.US) -> null
        else -> throw IllegalArgumentException("throttle只能作用于无返回值函数")
    }
}


/**
 * Potentially animate showing a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot of the view, and animate this in, only changing the visibility (and
 * thus layout) when the animation completes.
 */
fun NavigationBarView.show() {
    if (this is NavigationRailView) return
    if (isVisible) return

    val parent = parent as ViewGroup
    // View needs to be laid out to create a snapshot & know position to animate. If view isn't
    // laid out yet, need to do this manually.
    if (!isLaidOut) {
        measure(
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.AT_MOST)
        )
        layout(parent.left, parent.height - measuredHeight, parent.right, parent.height)
    }

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    drawable.setBounds(left, parent.height, right, parent.height + height)
    parent.overlay.add(drawable)
    ValueAnimator.ofInt(parent.height, top).apply {
        duration = ANIM_DURATION
        interpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.accelerate_decelerate
        )
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
            isVisible = true
        }
        start()
    }
}

/**
 * Potentially animate hiding a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot, instantly hide the view (so content lays out to fill), then animate
 * out the snapshot.
 */
fun NavigationBarView.hide() {
    if (this is NavigationRailView) return
    if (isGone) return

    if (!isLaidOut) {
        isGone = true
        return
    }

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    val parent = parent as ViewGroup
    drawable.setBounds(left, top, right, bottom)
    parent.overlay.add(drawable)
    isGone = true
    ValueAnimator.ofInt(top, parent.height).apply {
        duration = ANIM_DURATION
        interpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.accelerate_decelerate
        )
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
        }
        start()
    }
}


fun View.showToast(@StringRes resId: Int) {
    Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
}

val RecyclerView.ViewHolder.context: Context
    get() = itemView.context

inline val Int.dp: Int
    get() {
        return (this * LockSmithApplication.getContext().resources.displayMetrics.density + 0.5f).toInt()
    }

public fun Int.dpToPx(): Int = dpToPxPrecise().roundToInt()

public fun Int.dpToPxPrecise(): Float = (this * displayMetrics().density)

internal fun displayMetrics(): DisplayMetrics = Resources.getSystem().displayMetrics
