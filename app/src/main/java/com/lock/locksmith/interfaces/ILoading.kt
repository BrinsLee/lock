package com.lock.locksmith.interfaces

import android.content.DialogInterface

/**
 * @author lipeilin
 * @date 2024/4/28
 * @desc
 */
interface ILoading {

    fun showLoadingDialog()

    fun showLoadingDialog(listener: DialogInterface.OnDismissListener)

    fun dismissLoadingDialog()
}