package com.lock.locksmith.fragments.dialog

import android.app.Dialog
import android.content.DialogInterface.OnDismissListener
import android.os.Bundle
import android.view.ViewGroup
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

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val params = attributes
            // 设置宽度和高度为自适应内容
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            // 应用这些布局参数
            attributes = params
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return materialDialog(R.string.loading).setView(R.layout.loading).create()
    }
}