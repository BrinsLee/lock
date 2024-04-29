package com.lock.locksmith.extensions

import android.content.Context
import android.content.res.Configuration
import android.os.PowerManager
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lock.locksmith.BuildConfig
import com.lock.locksmith.R
import com.lock.locksmith.utils.PreferenceUtil

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */

fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

val Context.generalThemeValue
    get() = PreferenceUtil.getGeneralThemeValue(isSystemDarkModeEnabled())

fun Context.isSystemDarkModeEnabled(): Boolean {
    val isBatterySaverEnabled =
        (getSystemService<PowerManager>())?.isPowerSaveMode ?: false
    val isDarkModeEnabled =
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    return isBatterySaverEnabled or isDarkModeEnabled
}

fun Fragment.findNavController(@IdRes id: Int): NavController {
    val fragment = childFragmentManager.findFragmentById(id) as NavHostFragment
    return fragment.navController
}

fun AppCompatActivity.currentFragment(navHostId: Int): Fragment? {
    val navHostFragment: NavHostFragment =
        supportFragmentManager.findFragmentById(navHostId) as NavHostFragment
    return navHostFragment.childFragmentManager.fragments.firstOrNull()
}



fun Fragment.dip(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}

fun Fragment.dp2px(dp: Float): Int {
    return requireContext().dp2px(dp)
}

fun Fragment.materialDialog(): MaterialDialog {
    return MaterialDialog(requireContext())
        .cornerRadius(res = R.dimen.m3_dialog_corner_size)
}

fun Fragment.materialDialog(title: Int): MaterialAlertDialogBuilder {
    return if (BuildConfig.DEBUG) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MaterialAlertDialogTheme
        )
    } else {
        MaterialAlertDialogBuilder(
            requireContext()
        )
    }.setTitle(title)
}


fun Fragment.materialDialog(title: String): MaterialAlertDialogBuilder {
    return if (BuildConfig.DEBUG) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MaterialAlertDialogTheme
        )
    } else {
        MaterialAlertDialogBuilder(
            requireContext()
        )
    }.setTitle(title)
}


fun DialogFragment?.isShowing(): Boolean {
    return this != null && dialog != null && dialog!!.isShowing
}



fun AlertDialog.colorButtons(): AlertDialog {
    setOnShowListener {
        getButton(AlertDialog.BUTTON_POSITIVE).accentTextColor()
        getButton(AlertDialog.BUTTON_NEGATIVE).accentTextColor()
        getButton(AlertDialog.BUTTON_NEUTRAL).accentTextColor()
    }
    return this
}