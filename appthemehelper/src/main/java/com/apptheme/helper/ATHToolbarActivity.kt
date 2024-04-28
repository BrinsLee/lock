package com.apptheme.helper

import android.graphics.Color
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import com.apptheme.helper.utils.ColorUtil
import com.apptheme.helper.utils.handleOnCreateOptionsMenu
import com.apptheme.helper.utils.handleOnPrepareOptionsMenu
import com.google.android.material.R

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */
open class ATHToolbarActivity: ATHActivity() {

    private var toolbar: Toolbar? = null

    companion object {
        fun getToolbarBackgroundColor(toolbar: Toolbar?): Int {
            if (toolbar != null) {
                return ColorUtil.resolveColor(toolbar.context, R.attr.colorSurface)
            }
            return Color.BLACK
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val toolbar: Toolbar? = getATHToolbar()
        handleOnCreateOptionsMenu(
            this,
            toolbar,
            menu,
            getToolbarBackgroundColor(toolbar)
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        handleOnPrepareOptionsMenu(this, getATHToolbar())
        return super.onPrepareOptionsMenu(menu)
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        this.toolbar = toolbar
        super.setSupportActionBar(toolbar)
    }

    protected open fun getATHToolbar(): Toolbar? {
        return toolbar
    }
}