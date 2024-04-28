package com.lock.locksmith.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
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