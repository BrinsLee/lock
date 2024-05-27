package com.lock.locksmith.activities.base

import android.content.DialogInterface.OnDismissListener
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import com.lock.locksmith.extensions.isShowing
import com.lock.locksmith.fragments.dialog.LoadingDialogFragment
import com.lock.locksmith.interfaces.ILoading
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */
@AndroidEntryPoint
abstract class AbsBaseActivity : AbsThemeActivity(), ILoading{

    private var mLoadingDialog: LoadingDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)
    }

    abstract fun initData()

    abstract fun initView()

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_MENU && event.action == KeyEvent.ACTION_UP) {
            // showOverflowMenu()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(
                        v.windowToken,
                        0
                    )
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun showLoadingDialog() {
        if (!isFinishing) {
            if (mLoadingDialog == null) {
                mLoadingDialog = LoadingDialogFragment.create()
                mLoadingDialog!!.isCancelable = false
            }
            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog!!.show(supportFragmentManager, "loading")
            }
        }
    }

    override fun showLoadingDialog(listener: OnDismissListener) {
        if (!isFinishing) {
            if (mLoadingDialog == null) {
                mLoadingDialog = LoadingDialogFragment.create()
            }
            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog!!.show(supportFragmentManager, "loading")
                mLoadingDialog!!.onDismissListener = listener
            }
        }
    }

    override fun dismissLoadingDialog() {
        if (!isFinishing) {
            if (mLoadingDialog != null) {
                mLoadingDialog!!.dismiss()

                if (mLoadingDialog.isShowing()) {
                }
                mLoadingDialog = null
            }
        }
    }
}