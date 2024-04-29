package com.lock.locksmith.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.brins.blurlib.BlurView
import com.lock.locksmith.LockSmithApplication
import com.lock.locksmith.R
import com.lock.locksmith.databinding.LayoutBlurMaskBinding
import com.lock.locksmith.extensions.accentColor
import com.lock.locksmith.extensions.addAlpha
import com.lock.locksmith.extensions.showToast

/**
 * @author lipeilin
 * @date 2024/4/29
 * @desc
 */
class BlurMaskLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0
) : BlurView(context, attrs, def) {

    private var binding: LayoutBlurMaskBinding? = null

    var biometricPrompt: BiometricPrompt? = null

    var promptInfo: BiometricPrompt.PromptInfo? = null

    init {
        binding = LayoutBlurMaskBinding.inflate(LayoutInflater.from(context), this, true)
        updateOverlayColor(context.accentColor().addAlpha(0.12f))


    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        if (biometricPrompt != null && promptInfo != null) {
            biometricPrompt?.authenticate(promptInfo!!)

        }
        return super.performClick()
    }
}