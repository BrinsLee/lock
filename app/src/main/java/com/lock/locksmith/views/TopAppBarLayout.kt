package com.lock.locksmith.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.core.view.updateLayoutParams

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.shape.MaterialShapeDrawable
import com.lock.locksmith.R
import com.lock.locksmith.databinding.CollapsingAppbarLayoutBinding
import com.lock.locksmith.databinding.SimpleAppbarLayoutBinding

import com.lock.locksmith.views.TopAppBarLayout.AppBarMode.SIMPLE
import dev.chrisbanes.insetter.applyInsetter

class TopAppBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
) : AppBarLayout(context, attrs, defStyleAttr) {
    private var simpleAppbarBinding: SimpleAppbarLayoutBinding? = null
    private var collapsingAppbarBinding: CollapsingAppbarLayoutBinding? = null

    var mode: AppBarMode = SIMPLE

    val toolbar: MaterialToolbar
        get() = if (mode == AppBarMode.COLLAPSING) {
            collapsingAppbarBinding?.toolbar!!
        } else {
            simpleAppbarBinding?.toolbar!!
        }

    var title: CharSequence
        get() = if (mode == AppBarMode.COLLAPSING) {
            collapsingAppbarBinding?.collapsingToolbarLayout?.title.toString()
        } else {
            simpleAppbarBinding?.toolbar?.title.toString()
        }
        set(value) {
            if (mode == AppBarMode.COLLAPSING) {
                collapsingAppbarBinding?.collapsingToolbarLayout?.title = value
            } else {
                simpleAppbarBinding?.toolbar?.title = value
            }
        }


    var navigationIcon: Drawable?
        get() {
            return if (mode == AppBarMode.COLLAPSING) {
                collapsingAppbarBinding?.toolbar?.navigationIcon
            } else {
                simpleAppbarBinding?.toolbar?.navigationIcon
            }
        }
        set(value) {
            if (mode == AppBarMode.COLLAPSING) {
                collapsingAppbarBinding?.toolbar?.navigationIcon = value
            } else {
                simpleAppbarBinding?.toolbar?.navigationIcon = value
            }
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TopAppBarLayout)
        mode = if (ta.hasValue(R.styleable.TopAppBarLayout_topAppBarLayoutStyle)) {
            AppBarMode.entries[ta.getInt(R.styleable.TopAppBarLayout_topAppBarLayoutStyle, 0)]
        } else {
            SIMPLE
        }
        if (mode == AppBarMode.COLLAPSING) {
            collapsingAppbarBinding =
                CollapsingAppbarLayoutBinding.inflate(LayoutInflater.from(context), this, true)
            val isLandscape =
                context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            if (isLandscape) {
                fitsSystemWindows = false
            }

        } else {
            simpleAppbarBinding =
                SimpleAppbarLayoutBinding.inflate(LayoutInflater.from(context), this, true)
            simpleAppbarBinding?.root?.applyInsetter {
                type(navigationBars = true) {
                    padding(horizontal = true)
                }
            }
            statusBarForeground = MaterialShapeDrawable.createWithElevationOverlay(context)
        }

        if (ta.hasValue(R.styleable.TopAppBarLayout_title)) {
            title = ta.getString(R.styleable.TopAppBarLayout_title)!!
        }

        if (ta.hasValue(R.styleable.TopAppBarLayout_topNavigationIcon)) {
            navigationIcon = ta.getDrawable(R.styleable.TopAppBarLayout_topNavigationIcon)
        }
        ta.recycle()
    }

    fun pinWhenScrolled() {
        simpleAppbarBinding?.root?.updateLayoutParams<LayoutParams> {
            scrollFlags = SCROLL_FLAG_NO_SCROLL
        }
    }



    enum class AppBarMode {
        SIMPLE,
        COLLAPSING
    }
}
