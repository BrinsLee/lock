package com.lock.locksmith.fragments.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.apptheme.helper.common.prefs.ATEPreferenceFragmentCompat
import com.lock.locksmith.R
import com.lock.locksmith.extensions.dip
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author lipeilin
 * @date 2024/4/22
 * @desc
 */
@AndroidEntryPoint
abstract class AbsSettingFragment : ATEPreferenceFragmentCompat() {

    internal fun setSummary(preference: Preference, value: Any?) {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(stringValue)
            preference.setSummary(if (index >= 0) preference.entries[index] else null)
        } else {
            preference.summary = stringValue
        }
    }

    internal fun isSameValue(preference: Preference, value: Any?): Boolean {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(stringValue)
            return if (index >= 0) preference.entries[index] == preference.summary else false
        } else {
            return preference.summary == stringValue
        }
    }

    abstract fun invalidateSettings()

    protected fun setSummary(preference: Preference?) {
        preference?.let {
            setSummary(
                it, PreferenceManager
                    .getDefaultSharedPreferences(it.context)
                    .getString(it.key, "")
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDivider(ColorDrawable(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            listView.overScrollMode = View.OVER_SCROLL_NEVER
        }

        listView.updatePadding(bottom = dip(R.dimen.mini_player_height))
        listView.applyInsetter {
            type(navigationBars = true) {
                padding(vertical = true)
            }
        }
        invalidateSettings()
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {

            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    fun restartActivity() {
        activity?.recreate()

    }
}