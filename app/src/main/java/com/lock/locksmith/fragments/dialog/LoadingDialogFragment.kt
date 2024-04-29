package com.lock.locksmith.fragments.dialog

import android.app.Dialog
import android.content.DialogInterface.OnDismissListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.lock.locksmith.R
import com.lock.locksmith.extensions.materialDialog

/**
 * @author lipeilin
 * @date 2024/4/28
 * @desc
 */
class LoadingDialogFragment: DialogFragment() {

    var onDismissListener: OnDismissListener? = null
        set(value) {
            field = value
            dialog?.setOnDismissListener(value)
        }

    companion object {
        fun create(): LoadingDialogFragment {
            return LoadingDialogFragment().apply {

            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return materialDialog(R.string.loading).setView(R.layout.loading).create()
    }
}