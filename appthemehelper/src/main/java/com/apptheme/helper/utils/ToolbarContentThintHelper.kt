package com.apptheme.helper.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff.Mode.MULTIPLY
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.SearchView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.appcompat.R
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.WindowDecorActionBar
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.view.menu.BaseMenuPresenter
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.view.menu.MenuPresenter.Callback
import androidx.appcompat.view.menu.ShowableListMenu
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.appcompat.widget.ToolbarWidgetWrapper
import androidx.core.graphics.drawable.DrawableCompat
import com.apptheme.helper.ThemeStore
import java.lang.reflect.Field

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */



fun colorBackButton(toolbar: Toolbar) {
    val color: Int = ColorUtil.resolveColor(toolbar.context, R.attr.colorControlNormal)
    val colorFilter = PorterDuffColorFilter(color, MULTIPLY)
    for (i in 0 until toolbar.childCount) {
        val backButton = toolbar.getChildAt(i)
        if (backButton is ImageView) {
            backButton.drawable.colorFilter =
                colorFilter
        }
    }
}

/**
 * Use this method to colorize toolbar icons to the desired target color
 *
 * @param toolbarView       toolbar view being colored
 * @param toolbarIconsColor the target color of toolbar icons
 * @param activity          reference to activity needed to register observers
 */
@SuppressLint("RestrictedApi") fun colorizeToolbar(
    toolbarView: Toolbar, toolbarIconsColor: Int,
    activity: Activity
) {
    val colorFilter = PorterDuffColorFilter(
        toolbarIconsColor,
        MULTIPLY
    )

    for (i in 0 until toolbarView.childCount) {
        val v = toolbarView.getChildAt(i)

        //Step 1 : Changing the color of back button (or open drawer button).
        if (v is ImageButton) {
            //Action Bar back button
            v.drawable.colorFilter = colorFilter
        }

        if (v is ActionMenuView) {
            for (j in 0 until v.childCount) {
                //Step 2: Changing the color of any ActionMenuViews - icons that are not back button, nor text, nor overflow menu icon.
                //Colorize the ActionViews -> all icons that are NOT: back button | overflow menu

                val innerView = v.getChildAt(j)
                if (innerView is ActionMenuItemView) {
                    for (k in innerView.compoundDrawables.indices) {
                        if (innerView.compoundDrawables[k] != null) {
                            val finalK = k

                            //Important to set the color filter in seperate thread, by adding it to the message queue
                            //Won't work otherwise.
                            innerView.post {
                                innerView.compoundDrawables[finalK].colorFilter = colorFilter
                            }
                        }
                    }
                }
            }
        }

        //Step 3: Changing the color of title and subtitle.
        toolbarView.setTitleTextColor(
            ColorUtil.resolveColor(
                activity,
                android.R.attr.textColorPrimary
            )
        )
        toolbarView
            .setSubtitleTextColor(
                ColorUtil.resolveColor(
                    activity,
                    android.R.attr.textColorSecondary
                )
            )

        //Step 4: Changing the color of the Overflow Menu icon.
        setOverflowButtonColor(
            toolbarView,
            toolbarIconsColor
        )
    }
}

@SuppressLint("RestrictedApi") fun getSupportActionBarView(ab: ActionBar?): Toolbar? {
    if (ab !is WindowDecorActionBar) {
        return null
    }
    try {
        var field = WindowDecorActionBar::class.java.getDeclaredField("mDecorToolbar")
        field.isAccessible = true
        val wrapper = field[ab] as ToolbarWidgetWrapper
        field = ToolbarWidgetWrapper::class.java.getDeclaredField("mToolbar")
        field.isAccessible = true
        return field[wrapper] as Toolbar
    } catch (t: Throwable) {
        throw RuntimeException(
            "Failed to retrieve Toolbar from AppCompat support ActionBar: " + t.message, t
        )
    }
}

fun handleOnCreateOptionsMenu(
    context: Context,
    toolbar: Toolbar?,
    menu: Menu?,
    toolbarColor: Int
) {
    toolbar?.apply {
        setToolbarContentColorBasedOnToolbarColor(
            context,
            toolbar,
            menu,
            toolbarColor
        )
    }

}

fun handleOnCreateOptionsMenu(
    context: Context, toolbar: Toolbar?, menu: Menu?,
    @ColorInt toolbarContentColor: Int, @ColorInt titleTextColor: Int,
    @ColorInt subtitleTextColor: Int, @ColorInt menuWidgetColor: Int
) {
    setToolbarContentColor(
        context, toolbar, menu, toolbarContentColor, titleTextColor,
        subtitleTextColor, menuWidgetColor
    )
}

fun handleOnPrepareOptionsMenu(activity: Activity?, toolbar: Toolbar?) {
    handleOnPrepareOptionsMenu(activity, toolbar, ThemeStore.accentColor(activity!!))
}

fun handleOnPrepareOptionsMenu(
    activity: Activity?, toolbar: Toolbar?,
    widgetColor: Int
) {
    InternalToolbarContentTintUtil.applyOverflowMenuTint(activity!!, toolbar, widgetColor)
}

@SuppressLint("RestrictedApi") fun setToolbarContentColor(
    context: Context,
    toolbar: Toolbar?,
    menu: Menu?,
    @ColorInt toolbarContentColor: Int,
    @ColorInt titleTextColor: Int,
    @ColorInt subtitleTextColor: Int,
    @ColorInt menuWidgetColor: Int
) {
    var menu = menu
    if (toolbar == null) {
        return
    }

    if (menu == null) {
        menu = toolbar.menu
    }

    toolbar.setTitleTextColor(titleTextColor)
    toolbar.setSubtitleTextColor(subtitleTextColor)

    if (toolbar.navigationIcon != null) {
        // Tint the toolbar navigation icon (e.g. back, drawer, etc.)
        toolbar.navigationIcon =
            TintHelper.createTintedDrawable(toolbar.navigationIcon, toolbarContentColor)
    }

    InternalToolbarContentTintUtil.tintMenu(
        toolbar,
        menu,
        toolbarContentColor
    )
    InternalToolbarContentTintUtil.applyOverflowMenuTint(
        context,
        toolbar,
        menuWidgetColor
    )

    if (context is Activity) {
        InternalToolbarContentTintUtil.setOverflowButtonColor(
            context, toolbarContentColor
        )
    }

    try {
        // Tint immediate overflow menu items
        val menuField = Toolbar::class.java.getDeclaredField("mMenuBuilderCallback")
        menuField.isAccessible = true
        val presenterField = Toolbar::class.java.getDeclaredField("mActionMenuPresenterCallback")
        presenterField.isAccessible = true
        val menuViewField = Toolbar::class.java.getDeclaredField("mMenuView")
        menuViewField.isAccessible = true

        val currentPresenterCb = presenterField[toolbar] as Callback
        if (currentPresenterCb !is ATHMenuPresenterCallback) {
            val newPresenterCb: ATHMenuPresenterCallback =
                ATHMenuPresenterCallback(
                    context,
                    menuWidgetColor, currentPresenterCb, toolbar
                )
            val currentMenuCb = menuField[toolbar] as MenuBuilder.Callback
            toolbar.setMenuCallbacks(newPresenterCb, currentMenuCb)
            val menuView = menuViewField[toolbar] as ActionMenuView
            menuView?.setMenuCallbacks(newPresenterCb, currentMenuCb)
        }

        // OnMenuItemClickListener to tint submenu items
        val menuItemClickListener = Toolbar::class.java
            .getDeclaredField("mOnMenuItemClickListener")
        menuItemClickListener.isAccessible = true
        val currentClickListener = menuItemClickListener[toolbar] as OnMenuItemClickListener
        if (currentClickListener !is ATHOnMenuItemClickListener) {
            val newClickListener: ATHOnMenuItemClickListener =
                ATHOnMenuItemClickListener(
                    context,
                    menuWidgetColor, currentClickListener, toolbar
                )
            toolbar.setOnMenuItemClickListener(newClickListener)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun setToolbarContentColorBasedOnToolbarColor(
    context: Context,
    toolbar: Toolbar?,
    toolbarColor: Int
) {
    toolbar?.apply {
        setToolbarContentColorBasedOnToolbarColor(context, toolbar, null, toolbarColor)

    }
}

fun setToolbarContentColorBasedOnToolbarColor(
    context: Context,
    toolbar: Toolbar,
    menu: Menu?,
    toolbarColor: Int
) {
    setToolbarContentColorBasedOnToolbarColor(
        context, toolbar, menu, toolbarColor,
        ThemeStore.accentColor(context)
    )
}

fun setToolbarContentColorBasedOnToolbarColor(
    context: Context,
    toolbar: Toolbar,
    menu: Menu?,
    toolbarColor: Int,
    @ColorInt menuWidgetColor: Int
) {
    setToolbarContentColor(
        context, toolbar, menu, toolbarContentColor(context, toolbarColor),
        toolbarTitleColor(context, toolbarColor), toolbarSubtitleColor(context, toolbarColor),
        menuWidgetColor
    )
}

fun tintAllIcons(menu: Menu, color: Int) {
    for (i in 0 until menu.size()) {
        val item = menu.getItem(i)
        tintMenuItemIcon(color, item)
        tintShareIconIfPresent(color, item)
    }
}

@CheckResult @ColorInt fun toolbarContentColor(context: Context, @ColorInt toolbarColor: Int): Int {
    if (ColorUtil.isColorLight(toolbarColor)) {
        return toolbarSubtitleColor(context, toolbarColor)
    }
    return toolbarTitleColor(context, toolbarColor)
}

@CheckResult @ColorInt fun toolbarSubtitleColor(
    context: Context,
    @ColorInt toolbarColor: Int
): Int {
    return MaterialValueHelper
        .getSecondaryTextColor(context, ColorUtil.isColorLight(toolbarColor))
}

@CheckResult @ColorInt fun toolbarTitleColor(context: Context, @ColorInt toolbarColor: Int): Int {
    return MaterialValueHelper
        .getPrimaryTextColor(context, ColorUtil.isColorLight(toolbarColor))
}

private fun ToolbarContentTintHelper() {
}

private fun removeOnGlobalLayoutListener(
    v: View,
    listener: OnGlobalLayoutListener
) {
    v.viewTreeObserver.removeOnGlobalLayoutListener(listener)
}

private fun setOverflowButtonColor(toolbar: Toolbar, color: Int) {
    val drawable = toolbar.overflowIcon
    if (drawable != null) {
        // If we don't mutate the drawable, then all drawables with this id will have the ColorFilter
        drawable.mutate()
        drawable.setColorFilter(color, SRC_ATOP)
    }
}

private fun setOverflowButtonColor(
    activity: Activity, toolbar: Toolbar?,
    toolbarIconsColor: Int
) {
    val decorView = activity.window.decorView as ViewGroup
    val viewTreeObserver = decorView.viewTreeObserver
    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (toolbar != null && toolbar.overflowIcon != null) {
                val bg = DrawableCompat.wrap(
                    toolbar.overflowIcon!!
                )
                bg.setTint(toolbarIconsColor)
            }
            removeOnGlobalLayoutListener(decorView, this)
        }
    })
}

/**
 * It's important to set overflowDescription atribute in styles, so we can grab the reference to
 * the overflow icon. Check: res/values/styles.xml
 */
private fun setOverflowButtonColor(
    activity: Activity,
    colorFilter: PorterDuffColorFilter
) {
    val overflowDescription = activity
        .getString(R.string.abc_action_menu_overflow_description)
    val decorView = activity.window.decorView as ViewGroup
    val viewTreeObserver = decorView.viewTreeObserver
    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val outViews = ArrayList<View>()
            decorView.findViewsWithText(
                outViews, overflowDescription,
                View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
            )
            if (outViews.isEmpty()) {
                return
            }
            val overflowViewParent = outViews[0].parent as ActionMenuView
            overflowViewParent.overflowIcon!!.colorFilter = colorFilter
            removeOnGlobalLayoutListener(decorView, this)
        }
    })
}

private fun tintMenuItemIcon(color: Int, item: MenuItem) {
    val drawable = item.icon
    if (drawable != null) {
        val wrapped = DrawableCompat.wrap(drawable)
        drawable.mutate()
        wrapped.setTint(color)
        item.setIcon(drawable)
    }
}

private fun tintShareIconIfPresent(color: Int, item: MenuItem) {
    if (item.actionView != null) {
        val actionView = item.actionView
        val expandActivitiesButton =
            actionView!!.findViewById<View>(com.google.android.material.R.id.expand_activities_button)
        if (expandActivitiesButton != null) {
            val image =
                expandActivitiesButton.findViewById<ImageView>(com.google.android.material.R.id.image)
            if (image != null) {
                val drawable = image.drawable
                val wrapped = DrawableCompat.wrap(drawable)
                drawable.mutate()
                wrapped.setTint(color)
                image.setImageDrawable(drawable)
            }
        }
    }
}

object InternalToolbarContentTintUtil {
    @SuppressLint("RestrictedApi") fun applyOverflowMenuTint(
        context: Context, toolbar: Toolbar?,
        @ColorInt color: Int
    ) {
        if (toolbar == null) {
            return
        }
        toolbar.post(Runnable {
            try {
                val f1 =
                    Toolbar::class.java.getDeclaredField("mMenuView")
                f1.isAccessible = true
                val actionMenuView =
                    f1[toolbar] as ActionMenuView
                val f2 =
                    ActionMenuView::class.java.getDeclaredField("mPresenter")
                f2.isAccessible = true

                // Actually ActionMenuPresenter
                val presenter = f2[actionMenuView] as BaseMenuPresenter
                val f3 =
                    presenter.javaClass.getDeclaredField("mOverflowPopup")
                f3.isAccessible = true
                val overflowMenuPopupHelper = f3[presenter] as MenuPopupHelper
                setTintForMenuPopupHelper(
                    context,
                    overflowMenuPopupHelper,
                    color
                )

                val f4 =
                    presenter.javaClass.getDeclaredField("mActionButtonPopup")
                f4.isAccessible = true
                val subMenuPopupHelper = f4[presenter] as MenuPopupHelper
                setTintForMenuPopupHelper(
                    context,
                    subMenuPopupHelper,
                    color
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        })
    }

    fun setOverflowButtonColor(
        activity: Activity,
        @ColorInt color: Int
    ) {
        val overflowDescription = activity
            .getString(R.string.abc_action_menu_overflow_description)
        val decorView = activity.window.decorView as ViewGroup
        val viewTreeObserver = decorView.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val outViews = java.util.ArrayList<View>()
                decorView.findViewsWithText(
                    outViews, overflowDescription,
                    View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
                )
                if (outViews.isEmpty()) {
                    return
                }
                val overflow = outViews[0] as AppCompatImageView
                overflow.setImageDrawable(TintHelper.createTintedDrawable(overflow.drawable, color))
                ViewUtil.removeOnGlobalLayoutListener(decorView, this)
            }
        })
    }

    @SuppressLint("RestrictedApi") fun setTintForMenuPopupHelper(
        context: Context,
        @SuppressLint("RestrictedApi") menuPopupHelper: MenuPopupHelper?, @ColorInt color: Int
    ) {
        try {
            if (menuPopupHelper != null) {
                val listView = (menuPopupHelper.popup as ShowableListMenu).listView
                listView.viewTreeObserver
                    .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            try {
                                val checkboxField =
                                    ListMenuItemView::class.java.getDeclaredField("mCheckBox")
                                checkboxField.isAccessible = true
                                val radioButtonField = ListMenuItemView::class.java
                                    .getDeclaredField("mRadioButton")
                                radioButtonField.isAccessible = true

                                val isDark: Boolean = !ColorUtil.isColorLight(
                                    ColorUtil
                                        .resolveColor(context, android.R.attr.windowBackground)
                                )

                                for (i in 0 until listView.childCount) {
                                    val v = listView.getChildAt(i) as? ListMenuItemView ?: continue
                                    val iv = v

                                    val check = checkboxField[iv] as CheckBox
                                    if (check != null) {
                                        TintHelper.setTint(check, color, isDark)
                                        check.background = null
                                    }

                                    val radioButton = radioButtonField[iv] as RadioButton
                                    if (radioButton != null) {
                                        TintHelper.setTint(radioButton, color, isDark)
                                        radioButton.background = null
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            listView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun tintMenu(
        toolbar: Toolbar, menu: Menu?,
        @ColorInt color: Int
    ) {
        try {
            val field = Toolbar::class.java.getDeclaredField("mCollapseIcon")
            field.isAccessible = true
            val collapseIcon = field[toolbar] as Drawable
            if (collapseIcon != null) {
                field[toolbar] = TintHelper.createTintedDrawable(collapseIcon, color)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        if (menu != null && menu.size() > 0) {
            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                if (item.icon != null) {
                    item.setIcon(TintHelper.createTintedDrawable(item.icon, color))
                }
                // Search view theming
                if (item.actionView != null && (item.actionView is SearchView || item
                        .actionView is androidx.appcompat.widget.SearchView)
                ) {
                    SearchViewTintUtil.setSearchViewContentColor(item.actionView, color)
                }
            }
        }
    }

    object SearchViewTintUtil {
        fun setSearchViewContentColor(searchView: View?, @ColorInt color: Int) {
            if (searchView == null) {
                return
            }
            val cls: Class<*> = searchView.javaClass
            try {
                val mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView")
                mSearchSrcTextViewField.isAccessible = true
                val mSearchSrcTextView = mSearchSrcTextViewField[searchView] as EditText
                mSearchSrcTextView.setTextColor(color)
                mSearchSrcTextView.setHintTextColor(ColorUtil.adjustAlpha(color, 0.5f))
                TintHelper.setCursorTint(mSearchSrcTextView, color)

                var field = cls.getDeclaredField("mSearchButton")
                tintImageView(searchView, field, color)
                field = cls.getDeclaredField("mGoButton")
                tintImageView(searchView, field, color)
                field = cls.getDeclaredField("mCloseButton")
                tintImageView(searchView, field, color)
                field = cls.getDeclaredField("mVoiceButton")
                tintImageView(searchView, field, color)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        @Throws(java.lang.Exception::class) private fun tintImageView(
            target: Any,
            field: Field,
            @ColorInt color: Int
        ) {
            field.isAccessible = true
            val imageView = field[target] as ImageView
            if (imageView.drawable != null) {
                imageView
                    .setImageDrawable(
                        TintHelper.createTintedDrawable(imageView.drawable, color)
                    )
            }
        }
    }
}

private class ATHMenuPresenterCallback(
    private val mContext: Context, @param:ColorInt private val mColor: Int,
    @SuppressLint("RestrictedApi") private val mParentCb: Callback?, private val mToolbar: Toolbar
) : Callback {
    @SuppressLint("RestrictedApi")
    override fun onCloseMenu(menu: MenuBuilder, allMenusAreClosing: Boolean) {
        mParentCb?.onCloseMenu(menu, allMenusAreClosing)
    }

    @SuppressLint("RestrictedApi")
    override fun onOpenSubMenu(subMenu: MenuBuilder): Boolean {
        InternalToolbarContentTintUtil.applyOverflowMenuTint(mContext, mToolbar, mColor)
        return mParentCb != null && mParentCb.onOpenSubMenu(subMenu)
    }
}

private class ATHOnMenuItemClickListener(
    private val mContext: Context, @param:ColorInt private val mColor: Int,
    private val mParentListener: OnMenuItemClickListener?, private val mToolbar: Toolbar
) : OnMenuItemClickListener {
    override fun onMenuItemClick(item: MenuItem): Boolean {
        InternalToolbarContentTintUtil.applyOverflowMenuTint(mContext, mToolbar, mColor)
        return mParentListener != null && mParentListener.onMenuItemClick(item)
    }
}

