package com.apptheme.helper.common.prefs.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.preference.DialogPreference
import androidx.preference.DialogPreference.TargetFragment
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */
open class ATEPreferenceDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    companion object {

        val TAG = this::class.java.simpleName

        @JvmStatic
        protected val ARG_KEY: String = "key"

        fun newInstance(key: String?): ATEPreferenceDialogFragment {
            val fragment = ATEPreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    private var mWhichButtonClicked = 0

    protected var mPreference: DialogPreference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rawFragment = this.targetFragment
        check(rawFragment is TargetFragment) { "Target fragment must implement TargetFragment interface" }
        val fragment = rawFragment as TargetFragment
        arguments?.let {
            val key = it.getString(ARG_KEY) ?: ""
            this.mPreference = fragment.findPreference(key)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert
        )
            .setTitle(mPreference?.dialogTitle)
            .setIcon(mPreference?.dialogIcon)
            .setMessage(mPreference?.dialogMessage)
            .setPositiveButton(mPreference?.positiveButtonText, this)
            .setNegativeButton(mPreference?.negativeButtonText, this)
        this.onPrepareDialogBuilder(builder)
        val dialog = builder.create()
        if (this.needInputMethod()) {
            this.requestInputMethod(dialog)
        }
        return dialog
    }

    fun getPreference(): DialogPreference? {
        return this.mPreference
    }

    protected open fun onPrepareDialogBuilder(builder: MaterialAlertDialogBuilder) {
    }

    protected open fun needInputMethod(): Boolean {
        return false
    }

    protected open fun onDialogClosed(positiveResult: Boolean) {
    }

    private fun requestInputMethod(dialog: Dialog) {
        val window = dialog.window
        window!!.setSoftInputMode(5)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.i(TAG, "onDismiss: $mWhichButtonClicked")
        onDialogClosed(mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        Log.i(TAG, "onClick: $which")
        mWhichButtonClicked = which
        onDialogClosed(which == DialogInterface.BUTTON_POSITIVE)
    }
}