package com.apptheme.helper.common.views

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.View
import com.apptheme.helper.ThemeStore
import com.apptheme.helper.utils.ColorUtil
import com.apptheme.helper.utils.setCursorDrawable
import com.google.android.material.textfield.TextInputLayout

/**
 * @author lipeilin
 * @date 2024/4/28
 * @desc
 */
class ATETextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): TextInputLayout(context, attrs, defStyleAttr) {

    val accentColor = ThemeStore.accentColor(context)

    init {
        boxBackgroundColor = ColorUtil.withAlpha(accentColor, 0.12F)
        hintTextColor = ColorStateList.valueOf(accentColor)
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            cursorColor = ColorStateList.valueOf(accentColor)
        }
    }

    override fun addView(child: View, index: Int, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        editText?.apply {
            if (VERSION.SDK_INT < VERSION_CODES.Q) {
                setCursorDrawable(accentColor)
            }
        }
    }
}