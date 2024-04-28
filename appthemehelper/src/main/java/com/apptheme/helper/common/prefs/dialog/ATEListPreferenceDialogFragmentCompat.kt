package com.apptheme.helper.common.prefs.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import com.apptheme.helper.common.prefs.ATEListPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */
class ATEListPreferenceDialogFragmentCompat: ATEPreferenceDialogFragment() {

    companion object{
        val TAG = this::class.java.simpleName

        fun newInstance(key: String?): ATEListPreferenceDialogFragmentCompat {
            val fragment = ATEListPreferenceDialogFragmentCompat()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    private var mClickedDialogEntryIndex = 0

    private fun getListPreference(): ATEListPreference {
        return getPreference() as ATEListPreference
    }



    override fun onPrepareDialogBuilder(builder: MaterialAlertDialogBuilder) {
        super.onPrepareDialogBuilder(builder)

        val preference: ListPreference = getListPreference()

        check(!(preference.entries == null || preference.entryValues == null)) { "ListPreference requires an entries array and an entryValues array." }

        mClickedDialogEntryIndex = preference.findIndexOfValue(preference.value)
        builder.setSingleChoiceItems(
            preference.entries,
            mClickedDialogEntryIndex
        ) { dialog: DialogInterface?, which: Int ->
            mClickedDialogEntryIndex = which
            dismiss()
            onClick(dialog, which)
        }

        /*
     * The typical interaction for list-based dialogs is to have
     * click-on-an-item dismiss the dialog instead of the user having to
     * press 'Ok'.
     */
        builder.setPositiveButton(null, null)
        builder.setNegativeButton(null, null)
        builder.setNeutralButton(null, null)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        val preference: ListPreference = getListPreference()
        Log.i(TAG,
            "onDialogClosed: $positiveResult")
        if (positiveResult && mClickedDialogEntryIndex >= 0 && preference.entryValues != null) {
            val value = preference.entryValues[mClickedDialogEntryIndex].toString()
            Log.i(TAG, "onDialogClosed: value $value")
            if (preference.callChangeListener(value)) {
                preference.value = value
                Log.i(TAG, "onDialogClosed: set value ")
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        Log.i(TAG, "onClick: $which")
        mClickedDialogEntryIndex = which
        super.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
    }
}