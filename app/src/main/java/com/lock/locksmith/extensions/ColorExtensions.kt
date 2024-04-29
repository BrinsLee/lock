package com.lock.locksmith.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.Button
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.apptheme.helper.ThemeStore
import com.apptheme.helper.utils.ColorUtil

/**
* @author lipeilin
* @date 2024/4/21
* @desc
*/


fun Context.surfaceColor() = resolveColor(com.google.android.material.R.attr.colorSurface, Color.WHITE)

fun Context.resolveColor(@AttrRes attr: Int, fallBackColor: Int = 0) =
    ColorUtil.resolveColor(this, attr, fallBackColor)



inline val @receiver:ColorInt Int.isColorLight
    get() = ColorUtil.isColorLight(this)

inline val @receiver:ColorInt Int.lightColor
    get() = ColorUtil.withAlpha(this, 0.5F)

inline val @receiver:ColorInt Int.lighterColor
    get() = ColorUtil.lightenColor(this)

inline val @receiver:ColorInt Int.darkerColor
    get() = ColorUtil.darkenColor(this)

inline val Int.colorStateList: ColorStateList
    get() = ColorStateList.valueOf(this)

fun @receiver:ColorInt Int.addAlpha(alpha: Float): Int {
    return ColorUtil.withAlpha(this, alpha)
}


@CheckResult
fun Drawable.tint(@ColorInt color: Int): Drawable {
    val tintedDrawable = DrawableCompat.wrap(this).mutate()
    setTint(color)
    return tintedDrawable
}

@CheckResult
fun Drawable.tint(context: Context, @ColorRes color: Int): Drawable =
    tint(context.getColorCompat(color))

@ColorInt
fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.accentColor() = ThemeStore.accentColor(this)


fun Button.accentTextColor() {
    setTextColor(context.accentColor())
}