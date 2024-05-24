package com.lock.locksmith.fragments.setting

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.Preference

import com.apptheme.helper.utils.ColorUtil
import com.apptheme.helper.utils.VersionUtils
import com.afollestad.materialdialogs.color.colorChooser
import com.apptheme.helper.ACCENT_COLORS
import com.apptheme.helper.ACCENT_COLORS_SUB
import com.apptheme.helper.ThemeStore
import com.apptheme.helper.common.prefs.ATEColorPreference
import com.apptheme.helper.common.prefs.ATEListPreference
import com.lock.locksmith.ACCENT_COLOR
import com.lock.locksmith.GENERAL_THEME
import com.lock.locksmith.LANGUAGE_NAME
import com.lock.locksmith.R
import com.lock.locksmith.extensions.getTintedDrawable
import com.lock.locksmith.extensions.materialDialog
import com.lock.locksmith.utils.PreferenceUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/22
 * @desc
 */
@AndroidEntryPoint
class MainSettingFragment: AbsSettingFragment() {

    override fun invalidateSettings() {
        val generalTheme: Preference? = findPreference(GENERAL_THEME)
        generalTheme?.let {
            setSummary(it)
            it.setOnPreferenceChangeListener { _, newValue ->
                if (isSameValue(it, newValue)) {
                    setSummary(it, newValue)
                    true
                } else {
                    setSummary(it, newValue)
                    ThemeStore.markChanged(requireContext())

                    /*                if (VersionUtils.hasNougatMR()) {
                                        DynamicShortcutManager(requireContext()).updateDynamicShortcuts()
                                    }*/
                    restartActivity()
                    true
                }

            }
        }

        val accentColorPref: ATEColorPreference? = findPreference(ACCENT_COLOR)
        val accentColor = ThemeStore.accentColor(requireContext())
        accentColorPref?.setColor(accentColor, ColorUtil.darkenColor(accentColor))
        accentColorPref?.setOnPreferenceClickListener {
            materialDialog().show {
                colorChooser(
                    initialSelection = accentColor,
                    showAlphaSelector = false,
                    colors = ACCENT_COLORS,
                    subColors = ACCENT_COLORS_SUB, allowCustomArgb = false
                ) { _, color ->
                    val currentColor = ThemeStore.accentColor(requireContext())
                    if (currentColor != color) {
                        ThemeStore.editTheme(requireContext()).accentColor(color).commit()
                        /*                    if (VersionUtils.hasNougatMR())
                                                DynamicShortcutManager(requireContext()).updateDynamicShortcuts()*/
                        restartActivity()
                    }
                }
            }
            return@setOnPreferenceClickListener true
        }

        val languagePreference: ATEListPreference? = findPreference(LANGUAGE_NAME)
        languagePreference?.setOnPreferenceChangeListener { preference, newValue ->
            restartActivity()
            return@setOnPreferenceChangeListener true
        }

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        PreferenceUtil.languageCode =
            AppCompatDelegate.getApplicationLocales().toLanguageTags().ifEmpty { "auto" }
        addPreferencesFromResource(R.xml.pref_general)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val languagePreference: Preference? = findPreference(LANGUAGE_NAME)
        languagePreference?.apply {

            setOnPreferenceChangeListener { prefs, newValue ->
                setSummary(prefs, newValue)
                if (newValue as? String == "auto") {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                } else {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(
                            newValue as? String
                        )
                    )
                }
                true
            }
        }
    }
}