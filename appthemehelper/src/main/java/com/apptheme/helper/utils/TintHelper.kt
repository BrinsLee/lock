package com.apptheme.helper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.apptheme.helper.utils.ColorUtil.isWindowBackgroundDark

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.theme.helper.R

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */
object TintHelper {
    fun setTintAuto(
        view: View, @ColorInt color: Int,
        background: Boolean
    ) {
        setTintAuto(view, color, background, isWindowBackgroundDark(view.context))
    }

    fun setTintAuto(
        view: View, @ColorInt color: Int,
        background: Boolean, isDark: Boolean
    ) {
        var background = background
        if (!background) {
            when (view) {
                is FloatingActionButton -> {
                    TintHelper.setTint(view as FloatingActionButton, color, isDark)
                }

                is RadioButton -> {
                    TintHelper.setTint(view as RadioButton, color, isDark)
                }

                is SeekBar -> {
                    TintHelper.setTint(view as SeekBar, color, isDark)
                }

                is ProgressBar -> {
                    TintHelper.setTint(view as ProgressBar, color)
                }

                is EditText -> {
                    TintHelper.setTint(view as EditText, color, isDark)
                }

                is CheckBox -> {
                    TintHelper.setTint(view as CheckBox, color, isDark)
                }

                is ImageView -> {
                    TintHelper.setTint(view as ImageView, color)
                }

                is MaterialSwitch -> {
                    TintHelper.setTint(view as MaterialSwitch, color, isDark)
                }

                is SwitchCompat -> {
                    TintHelper.setTint(view as SwitchCompat, color, isDark)
                }

                else -> {
                    background = true
                }
            }

            if (!background && view.background is RippleDrawable) {
                // Ripples for the above views (e.g. when you tap and hold a switch or checkbox)
                val rd = view.background as RippleDrawable
                @SuppressLint("PrivateResource") val unchecked = ContextCompat.getColor(
                    view.context,
                    if (isDark) androidx.appcompat.R.color.ripple_material_dark else androidx.appcompat.R.color.ripple_material_light
                )
                val checked: Int = ColorUtil.adjustAlpha(color, 0.4f)
                val sl = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_activated, -android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_activated),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(
                        unchecked,
                        checked,
                        checked
                    )
                )
                rd.setColor(sl)
            }
        }
        if (background) {
            // Need to tint the background of a view
            if (view is FloatingActionButton || view is Button) {
                TintHelper.setTintSelector(view, color, false, isDark)
            } else if (view.background != null) {
                var drawable = view.background
                if (drawable != null) {
                    drawable = TintHelper.createTintedDrawable(drawable, color)
                    ViewUtil.setBackgroundCompat(view, drawable)
                }
            }
        }
    }

    private fun setTint(view: FloatingActionButton, color: Int, isDark: Boolean) {
        view.imageTintList = ColorStateList.valueOf(color)
    }

    fun setTint(radioButton: RadioButton, @ColorInt color: Int, useDarker: Boolean) {
        val sl = ColorStateList(
            arrayOf<IntArray>(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
            ), intArrayOf( // Rdio button includes own alpha for disabled state
                ColorUtil.stripAlpha(
                    ContextCompat.getColor(
                        radioButton.context,
                        if (useDarker) R.color.ate_control_disabled_dark else R.color.ate_control_disabled_light
                    )
                ),
                ContextCompat.getColor(
                    radioButton.context,
                    if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
                ),
                color
            )
        )
        radioButton.buttonTintList = sl
    }

    fun setTint(switchView: SwitchCompat, @ColorInt color: Int, useDarker: Boolean) {
        if (switchView.trackDrawable != null) {
            switchView.trackDrawable = TintHelper.modifySwitchDrawable(
                switchView.context,
                switchView.trackDrawable, color, false, true, useDarker
            )
        }
        if (switchView.thumbDrawable != null) {
            switchView.thumbDrawable = TintHelper.modifySwitchDrawable(
                switchView.context,
                switchView.thumbDrawable, color, true, true, useDarker
            )
        }
    }

    fun setTint(seekBar: SeekBar, @ColorInt color: Int, useDarker: Boolean) {
        val s1: ColorStateList = TintHelper.getDisabledColorStateList(
            color, ContextCompat.getColor(
                seekBar.context,
                if (useDarker) R.color.ate_control_disabled_dark else R.color.ate_control_disabled_light
            )
        )
        seekBar.thumbTintList = s1
        seekBar.progressTintList = s1
    }



    fun setTint(image: ImageView, @ColorInt color: Int) {
        image.setColorFilter(color, SRC_ATOP)
    }

    fun setTint(progressBar: ProgressBar, @ColorInt color: Int) {
        setTint(progressBar, color, false)
    }

    fun setTint(progressBar: ProgressBar, @ColorInt color: Int, skipIndeterminate: Boolean) {
        val sl = ColorStateList.valueOf(color)
        progressBar.progressTintList = sl
        progressBar.secondaryProgressTintList = sl
        if (!skipIndeterminate) {
            progressBar.indeterminateTintList = sl
        }
    }

    fun setTint(editText: EditText, @ColorInt color: Int, useDarker: Boolean) {
        val editTextColorStateList = ColorStateList(
            arrayOf<IntArray>(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(
                    android.R.attr.state_enabled,
                    -android.R.attr.state_pressed,
                    -android.R.attr.state_focused
                ),
                intArrayOf()
            ), intArrayOf(
                ContextCompat.getColor(
                    editText.context,
                    if (useDarker) R.color.ate_text_disabled_dark else R.color.ate_text_disabled_light
                ),
                ContextCompat.getColor(
                    editText.context,
                    if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
                ),
                color
            )
        )
        if (editText is AppCompatEditText) {
            (editText as AppCompatEditText).supportBackgroundTintList =
                editTextColorStateList
        } else {
            editText.backgroundTintList = editTextColorStateList
        }
        TintHelper.setCursorTint(editText, color)
    }

    @SuppressLint("SoonBlockedPrivateApi")
    fun setCursorTint(editText: EditText, @ColorInt color: Int) {
        try {
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor[editText]
            val clazz: Class<*> = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true
            val drawables = arrayOfNulls<Drawable>(2)
            drawables[0] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[0] = TintHelper.createTintedDrawable(drawables[0], color)
            drawables[1] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[1] = TintHelper.createTintedDrawable(drawables[1], color)
            fCursorDrawable[editor] = drawables
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @CheckResult fun createTintedDrawable(
        context: Context?,
        @DrawableRes res: Int, @ColorInt color: Int
    ): Drawable? {
        val drawable = ContextCompat.getDrawable(context!!, res)
        return createTintedDrawable(drawable, color)
    }

    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary because Drawables with the same resource have shared states otherwise.
    @CheckResult fun createTintedDrawable(drawable: Drawable?, @ColorInt color: Int): Drawable? {
        var drawable1 = drawable ?: return null
        drawable1 = DrawableCompat.wrap(drawable1.mutate())
        drawable1.setTintMode(SRC_IN)
        drawable1.setTint(color)
        return drawable1
    }

    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary because Drawables with the same resource have shared states otherwise.
    @CheckResult fun createTintedDrawable(drawable: Drawable?, sl: ColorStateList): Drawable? {
        if (drawable == null) {
            return null
        }
        val temp = DrawableCompat.wrap(drawable.mutate())
        temp.setTintList(sl)
        return temp
    }

    fun setTint(box: CheckBox, @ColorInt color: Int, useDarker: Boolean) {
        val sl = ColorStateList(
            arrayOf<IntArray>(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
            ), intArrayOf(
                ContextCompat.getColor(
                    box.context,
                    if (useDarker) R.color.ate_control_disabled_dark else R.color.ate_control_disabled_light
                ),
                ContextCompat.getColor(
                    box.context,
                    if (useDarker) R.color.ate_control_normal_dark else R.color.ate_control_normal_light
                ),
                color
            )
        )
        box.buttonTintList = sl
    }

    private fun getDisabledColorStateList(
        @ColorInt normal: Int,
        @ColorInt disabled: Int
    ): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                disabled,
                normal
            )
        )
    }

    private fun modifySwitchDrawable(
        context: Context, from: Drawable, @ColorInt tint: Int,
        thumb: Boolean, compatSwitch: Boolean, useDarker: Boolean
    ): Drawable? {
        val sl: ColorStateList =
            TintHelper.createSwitchDrawableTintList(context, tint, thumb, compatSwitch, useDarker)
        return createTintedDrawable(from, sl)
    }

    private fun createSwitchDrawableTintList(
        context: Context, @ColorInt tint: Int,
        thumb: Boolean, compatSwitch: Boolean, useDarker: Boolean
    ): ColorStateList {
        var tint = tint
        val lighterTint: Int = ColorUtil.blendColors(tint, Color.WHITE, 0.4f)
        val darkerTint: Int = ColorUtil.shiftColor(tint, 0.8f)
        tint = if (useDarker) {
            if ((compatSwitch && !thumb)) lighterTint else darkerTint
        } else {
            if ((compatSwitch && !thumb)) darkerTint else Color.WHITE
        }
        val disabled: Int
        var normal: Int
        if (thumb) {
            disabled = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_thumb_disabled_dark else R.color.ate_switch_thumb_disabled_light
            )
            normal = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_thumb_normal_dark else R.color.ate_switch_thumb_normal_light
            )
        } else {
            disabled = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_track_disabled_dark else R.color.ate_switch_track_disabled_light
            )
            normal = ContextCompat.getColor(
                context,
                if (useDarker) R.color.ate_switch_track_normal_dark else R.color.ate_switch_track_normal_light
            )
        }

        // Stock switch includes its own alpha
        if (!compatSwitch) {
            normal = ColorUtil.stripAlpha(normal)
        }

        return ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(
                    android.R.attr.state_enabled, -android.R.attr.state_activated,
                    -android.R.attr.state_checked
                ),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
            ),
            intArrayOf(
                disabled,
                normal,
                tint,
                tint
            )
        )
    }

    fun setTintSelector(
        view: View, @ColorInt color: Int, darker: Boolean,
        useDarkTheme: Boolean
    ) {
        val isColorLight: Boolean = ColorUtil.isColorLight(color)
        val disabled = ContextCompat.getColor(
            view.context,
            if (useDarkTheme) R.color.ate_button_disabled_dark else R.color.ate_button_disabled_light
        )
        val pressed: Int = ColorUtil.shiftColor(color, if (darker) 0.9f else 1.1f)
        val activated: Int = ColorUtil.shiftColor(color, if (darker) 1.1f else 0.9f)
        val rippleColor: Int = TintHelper.getDefaultRippleColor(view.context, isColorLight)
        val textColor = ContextCompat.getColor(
            view.context,
            if (isColorLight) R.color.ate_primary_text_light else R.color.ate_primary_text_dark
        )

        val sl: ColorStateList
        if (view is Button) {
            sl = getDisabledColorStateList(color, disabled)
            if (view.getBackground() is RippleDrawable) {
                val rd = view.getBackground() as RippleDrawable
                rd.setColor(ColorStateList.valueOf(rippleColor))
            }

            // Disabled text color state for buttons, may get overridden later by ATE tags
            view.setTextColor(
                getDisabledColorStateList(
                    textColor, ContextCompat.getColor(
                        view.getContext(),
                        if (useDarkTheme) R.color.ate_button_text_disabled_dark else R.color.ate_button_text_disabled_light
                    )
                )
            )
        } else if (view is FloatingActionButton) {
            // FloatingActionButton doesn't support disabled state?
            sl = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_pressed)
                ), intArrayOf(
                    color,
                    pressed
                )
            )

            val fab = view
            fab.rippleColor = rippleColor
            fab.backgroundTintList = sl
            if (fab.drawable != null) {
                fab.setImageDrawable(createTintedDrawable(fab.drawable, textColor))
            }
            return
        } else {
            sl = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
                ),
                intArrayOf(
                    disabled,
                    color,
                    pressed,
                    activated,
                    activated
                )
            )
        }

        var drawable = view.background
        if (drawable != null) {
            drawable = createTintedDrawable(drawable, sl)
            ViewUtil.setBackgroundCompat(view, drawable)
        }

        if (view is TextView && view !is Button) {
            view.setTextColor(
                getDisabledColorStateList(
                    textColor, ContextCompat.getColor(
                        view.getContext(),
                        if (isColorLight) R.color.ate_text_disabled_light else R.color.ate_text_disabled_dark
                    )
                )
            )
        }
    }

    @SuppressLint("PrivateResource") @ColorInt private fun getDefaultRippleColor(
        context: Context,
        useDarkRipple: Boolean
    ): Int {
        // Light ripple is actually translucent black, and vice versa
        return ContextCompat.getColor(
            context,
            if (useDarkRipple) androidx.appcompat.R.color.ripple_material_light else androidx.appcompat.R.color.ripple_material_dark
        )
    }
}