package com.lock.locksmith.utils

import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.lock.locksmith.BLACK_THEME
import com.lock.locksmith.GENERAL_THEME
import com.lock.locksmith.LANGUAGE_NAME
import com.lock.locksmith.LockSmithApplication
import com.lock.locksmith.extensions.getStringOrDefault
import com.lock.locksmith.utils.theme.ThemeMode

/**
* @author lipeilin
* @date 2024/4/21
* @desc
 *
*/object PreferenceUtil {
    var isFullScreenMode: Boolean = false

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LockSmithApplication.getContext())

    private val isBlackMode
        get() = sharedPreferences.getBoolean(
            BLACK_THEME, false
        )

    var languageCode: String
        get() = sharedPreferences.getString(LANGUAGE_NAME, "auto") ?: "auto"
        set(value) = sharedPreferences.edit {
            putString(LANGUAGE_NAME, value)
        }

    fun getGeneralThemeValue(isSystemDark: Boolean): ThemeMode {
        val themeMode: String =
            sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")
        return when (themeMode) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            "auto" -> ThemeMode.AUTO
            else -> ThemeMode.AUTO
        }
    }

}


