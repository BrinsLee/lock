package com.lock.locksmith.fragments.dialog

import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.lock.locksmith.EXTRA_MESSAGE
import com.lock.locksmith.EXTRA_TITLE
import com.lock.locksmith.R
import com.lock.locksmith.extensions.materialDialog

/**
 * @author lipeilin
 * @date 2024/4/28
 * @desc
 */
class ErrorInfoDialogFragment : DialogFragment() {

    companion object {
        fun create(title: String, message: String): ErrorInfoDialogFragment {
            return ErrorInfoDialogFragment().apply {
                arguments = bundleOf(
                    EXTRA_TITLE to title,
                    EXTRA_MESSAGE to message
                )
            }
        }
    }

    private var title: String? = null

    private var message: String? = null

    interface DialogDismissListener {
        fun onDialogDismissed()
    }

    var dialogDismissListener: DialogDismissListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(EXTRA_TITLE)

        message = arguments?.getString(EXTRA_MESSAGE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        materialDialog(title ?: "").apply {
            setMessage(message ?: "")
            setPositiveButton(R.string.ok) { dialog, which -> dismiss() }
        }.create()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogDismissListener?.onDialogDismissed()
    }
}