package com.apptheme.helper.utils

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

object ViewUtil {
    fun removeOnGlobalLayoutListener(v: View, listener: ViewTreeObserver.OnGlobalLayoutListener) {
        v.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    fun setBackgroundCompat(view: View, drawable: Drawable?) {
        view.background = drawable
    }

}

@SuppressLint("SoonBlockedPrivateApi")
fun EditText.setCursorDrawable(color: Int) {
    try {
        // 反射获取 TextView 类中的 mCursorDrawableRes 字段
        val cursorDrawableResField = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        cursorDrawableResField.isAccessible = true
        val cursorDrawableRes = cursorDrawableResField.getInt(this)

        // 使用获取到的资源 ID 获取 Drawable
        val cursorDrawable = ContextCompat.getDrawable(context, cursorDrawableRes)
        val wrappedDrawable = DrawableCompat.wrap(cursorDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, color)

        // 反射获取 TextView 的 mEditor 字段
        val editorField = TextView::class.java.getDeclaredField("mEditor")
        editorField.isAccessible = true
        val editor = editorField.get(this)

        // 反射设置新的光标 Drawable 数组
        val cursorDrawableField = editor::class.java.getDeclaredField("mCursorDrawable")
        cursorDrawableField.isAccessible = true
        cursorDrawableField.set(editor, arrayOf(wrappedDrawable, wrappedDrawable))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}