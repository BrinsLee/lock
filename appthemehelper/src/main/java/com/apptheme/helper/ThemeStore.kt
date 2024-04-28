package com.apptheme.helper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.apptheme.helper.utils.ColorUtil
import com.apptheme.helper.utils.ColorUtil.isWindowBackgroundDark
import com.apptheme.helper.utils.ColorUtil.resolveColor

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */
class ThemeStore private constructor(private val mContext: Context): ThemeStoreInterface, ThemeStorePrefKeys{

    private val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(ThemeStorePrefKeys.CONFIG_PREFS_KEY_DEFAULT, Context.MODE_PRIVATE)

    private val mEditor: SharedPreferences.Editor = sharedPreferences.edit()


    companion object {

        fun editTheme(context: Context): ThemeStore {
            return ThemeStore(context)
        }


        fun markChanged(context: Context) {
            ThemeStore(context).commit()
        }

        @CheckResult
        fun preferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                ThemeStorePrefKeys.CONFIG_PREFS_KEY_DEFAULT,
                Context.MODE_PRIVATE
            )
        }

        @CheckResult
        @StyleRes
        fun activityTheme(context: Context): Int {
            return preferences(context).getInt(ThemeStorePrefKeys.KEY_ACTIVITY_THEME, 0)
        }

        @CheckResult
        @ColorInt
        fun primaryColor(context: Context): Int {
            return preferences(context).getInt(
                ThemeStorePrefKeys.KEY_PRIMARY_COLOR,
                resolveColor(context, androidx.appcompat.R.attr.colorPrimary, Color.parseColor("#455A64"))
            )
        }

        @CheckResult
        @ColorInt
        fun accentColor(context: Context): Int {
            val color = preferences(context).getInt(
                ThemeStorePrefKeys.KEY_ACCENT_COLOR,
                resolveColor(context, androidx.appcompat.R.attr.colorAccent, Color.parseColor("#263238"))
            )
            return if (isWindowBackgroundDark(context)) ColorUtil.desaturateColor(
                color,
                0.4f
            ) else color
        }


        @CheckResult
        fun autoGeneratePrimaryDark(context: Context): Boolean {
            return preferences(context).getBoolean(ThemeStorePrefKeys.KEY_AUTO_GENERATE_PRIMARYDARK, true)
        }

        @CheckResult
        @ColorInt
        fun navigationBarColor(context: Context): Int {
            return if (!coloredNavigationBar(context)) {
                Color.BLACK
            } else preferences(context).getInt(
                ThemeStorePrefKeys.KEY_NAVIGATION_BAR_COLOR,
                primaryColor(context)
            )
        }

        @CheckResult
        fun coloredStatusBar(context: Context): Boolean {
            return preferences(context).getBoolean(
                ThemeStorePrefKeys.KEY_APPLY_PRIMARYDARK_STATUSBAR,
                true
            )
        }

        @CheckResult
        fun coloredNavigationBar(context: Context): Boolean {
            return preferences(context).getBoolean(ThemeStorePrefKeys.KEY_APPLY_PRIMARY_NAVBAR, false)
        }

        @CheckResult
        fun isConfigured(context: Context): Boolean {
            return preferences(context).getBoolean(ThemeStorePrefKeys.IS_CONFIGURED_KEY, false)
        }


        @CheckResult
        @ColorInt
        fun textColorPrimary(context: Context): Int {
            return preferences(context).getInt(
                ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY,
                resolveColor(context, android.R.attr.textColorPrimary)
            )
        }

        @CheckResult
        @ColorInt
        fun textColorPrimaryInverse(context: Context): Int {
            return preferences(context).getInt(
                ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY_INVERSE,
                resolveColor(context, android.R.attr.textColorPrimaryInverse)
            )
        }

        @CheckResult
        @ColorInt
        fun textColorSecondary(context: Context): Int {
            return preferences(context).getInt(
                ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY,
                resolveColor(context, android.R.attr.textColorSecondary)
            )
        }

        @CheckResult
        @ColorInt
        fun textColorSecondaryInverse(context: Context): Int {
            return preferences(context).getInt(
                ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY_INVERSE,
                resolveColor(context, android.R.attr.textColorSecondaryInverse)
            )
        }

        fun isConfigured(
            context: Context, @IntRange(
                from = 0,
                to = Integer.MAX_VALUE.toLong()
            ) version: Int
        ): Boolean {
            val prefs = preferences(context)
            val lastVersion = prefs.getInt(ThemeStorePrefKeys.IS_CONFIGURED_VERSION_KEY, -1)
            if (version > lastVersion) {
                prefs.edit { putInt(ThemeStorePrefKeys.IS_CONFIGURED_VERSION_KEY, version) }
                return false
            }
            return true
        }
    }

    override fun activityTheme(theme: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_ACTIVITY_THEME, theme)
        return this
    }

    override fun primaryColor(color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_PRIMARY_COLOR, color)
        if (autoGeneratePrimaryDark(mContext))
            primaryColorDark(ColorUtil.darkenColor(color))
        return this
    }

    override fun primaryColorRes(colorRes: Int): ThemeStore {
        return primaryColor(ContextCompat.getColor(mContext, colorRes))
    }

    override fun primaryColorAttr(colorAttr: Int): ThemeStore {
        return primaryColor(resolveColor(mContext, colorAttr))
    }

    override fun autoGeneratePrimaryDark(autoGenerate: Boolean): ThemeStore {
        mEditor.putBoolean(ThemeStorePrefKeys.KEY_AUTO_GENERATE_PRIMARYDARK, autoGenerate)
        return this
    }

    override fun primaryColorDark(color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_PRIMARY_COLOR_DARK, color)
        return this
    }

    override fun primaryColorDarkRes(colorRes: Int): ThemeStore {
        return primaryColorDark(ContextCompat.getColor(mContext, colorRes))
    }

    override fun primaryColorDarkAttr(colorAttr: Int): ThemeStore {
        return primaryColorDark(resolveColor(mContext, colorAttr))
    }

    override fun accentColor(color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_ACCENT_COLOR, color)
        return this
    }

    override fun accentColorRes(colorRes: Int): ThemeStore {
        return accentColor(ContextCompat.getColor(mContext, colorRes))
    }

    override fun accentColorAttr(colorAttr: Int): ThemeStore {
        return accentColor(resolveColor(mContext, colorAttr))
    }

    override fun statusBarColor(color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_STATUS_BAR_COLOR, color)
        return this
    }

    override fun statusBarColorRes(colorRes: Int): ThemeStore {
        return statusBarColor(ContextCompat.getColor(mContext, colorRes))
    }

    override fun statusBarColorAttr(colorAttr: Int): ThemeStore {
        return statusBarColor(resolveColor(mContext, colorAttr))
    }

    override fun navigationBarColor(color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_NAVIGATION_BAR_COLOR, color)
        return this
    }

    override fun navigationBarColorRes(colorRes: Int): ThemeStore {
        return navigationBarColor(ContextCompat.getColor(mContext, colorRes))
    }

    override fun navigationBarColorAttr(colorAttr: Int): ThemeStore {
        return navigationBarColor(resolveColor(mContext, colorAttr))
    }

    override fun textColorPrimary(color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY, color)
        return this
    }

    override fun textColorPrimaryRes(colorRes: Int): ThemeStore {
        return textColorPrimary(ContextCompat.getColor(mContext, colorRes))
    }

    override fun textColorPrimaryAttr(colorAttr: Int): ThemeStore {
        return textColorPrimary(resolveColor(mContext, colorAttr))
    }

    override fun textColorPrimaryInverse(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_PRIMARY_INVERSE, color)
        return this
    }

    override fun textColorPrimaryInverseRes(@ColorRes colorRes: Int): ThemeStore {
        return textColorPrimaryInverse(ContextCompat.getColor(mContext, colorRes))
    }

    override fun textColorPrimaryInverseAttr(@AttrRes colorAttr: Int): ThemeStore {
        return textColorPrimaryInverse(resolveColor(mContext, colorAttr))
    }

    override fun textColorSecondary(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY, color)
        return this
    }

    override fun textColorSecondaryRes(@ColorRes colorRes: Int): ThemeStore {
        return textColorSecondary(ContextCompat.getColor(mContext, colorRes))
    }

    override fun textColorSecondaryAttr(@AttrRes colorAttr: Int): ThemeStore {
        return textColorSecondary(resolveColor(mContext, colorAttr))
    }

    override fun textColorSecondaryInverse(@ColorInt color: Int): ThemeStore {
        mEditor.putInt(ThemeStorePrefKeys.KEY_TEXT_COLOR_SECONDARY_INVERSE, color)
        return this
    }

    override fun textColorSecondaryInverseRes(@ColorRes colorRes: Int): ThemeStore {
        return textColorSecondaryInverse(ContextCompat.getColor(mContext, colorRes))
    }

    override fun textColorSecondaryInverseAttr(@AttrRes colorAttr: Int): ThemeStore {
        return textColorSecondaryInverse(resolveColor(mContext, colorAttr))
    }

    override fun coloredStatusBar(colored: Boolean): ThemeStore {
        mEditor.putBoolean(ThemeStorePrefKeys.KEY_APPLY_PRIMARYDARK_STATUSBAR, colored)
        return this
    }

    override fun coloredNavigationBar(applyToNavBar: Boolean): ThemeStore {
        mEditor.putBoolean(ThemeStorePrefKeys.KEY_APPLY_PRIMARY_NAVBAR, applyToNavBar)
        return this
    }


    override fun commit() {
        mEditor.putLong(ThemeStorePrefKeys.VALUES_CHANGED, System.currentTimeMillis())
            .putBoolean(ThemeStorePrefKeys.IS_CONFIGURED_KEY, true)
            .commit()
    }

    override fun apply() {
        mEditor.putLong(ThemeStorePrefKeys.VALUES_CHANGED, System.currentTimeMillis())
            .putBoolean(ThemeStorePrefKeys.IS_CONFIGURED_KEY, true)
            .apply()
    }
}