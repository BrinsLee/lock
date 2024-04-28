package com.apptheme.helper.common.prefs

import android.annotation.SuppressLint
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.apptheme.helper.common.prefs.dialog.ATEListPreferenceDialogFragmentCompat
import com.apptheme.helper.common.prefs.dialog.ATEPreferenceDialogFragment

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */
abstract class ATEPreferenceFragmentCompat: PreferenceFragmentCompat() {

    @SuppressLint("RestrictedApi")
    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (callbackFragment is OnPreferenceStartFragmentCallback) {
            (callbackFragment as OnPreferenceDisplayDialogCallback).onPreferenceDisplayDialog(
                this, preference
            )
        }

        if (this.activity is OnPreferenceDisplayDialogCallback) {
            (this.activity as OnPreferenceDisplayDialogCallback).onPreferenceDisplayDialog(this, preference)
            return
        }

        if (fragmentManager?.findFragmentByTag("androidx.preference.PreferenceFragment.DIALOG") == null) {
            val dialogFragment: DialogFragment? = onCreatePreferenceDialog(preference)

            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.show(
                    this.requireFragmentManager(),
                    "androidx.preference.PreferenceFragment.DIALOG"
                )
                return
            }
        }
        super.onDisplayPreferenceDialog(preference)
    }

    fun onCreatePreferenceDialog(preference: Preference): DialogFragment? {
        if (preference is ATEListPreference) {
            return ATEListPreferenceDialogFragmentCompat.newInstance(preference.getKey())
        } else if (preference is ATEDialogPreference) {
            return ATEPreferenceDialogFragment.newInstance(preference.getKey())
        }
        return null
    }
}