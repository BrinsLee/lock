package com.lock.locksmith.utils.theme

import android.content.Context
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import com.lock.locksmith.R
import com.lock.locksmith.extensions.generalThemeValue
import com.lock.locksmith.utils.PreferenceUtil
import com.lock.locksmith.utils.theme.ThemeMode.AUTO
import com.lock.locksmith.utils.theme.ThemeMode.BLACK
import com.lock.locksmith.utils.theme.ThemeMode.DARK
import com.lock.locksmith.utils.theme.ThemeMode.LIGHT

/**
 * @author lipeilin
 * @date 2024/4/22
 * @desc
 */

@StyleRes
fun Context.getThemeResValue(): Int =
    when (generalThemeValue) {
        LIGHT -> R.style.Theme_Locksmith_Light
        DARK -> R.style.Theme_Locksmith_Base
        BLACK -> R.style.Theme_Locksmith_Black
        AUTO -> R.style.Theme_Locksmith_FollowSystem
    }

fun Context.getNightMode(): Int = when (generalThemeValue) {
    LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    DARK -> AppCompatDelegate.MODE_NIGHT_YES
    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
}