package com.lock.locksmith.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.FontRes
import com.google.android.material.textview.MaterialTextView
import com.lock.locksmith.R
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
* @author lipeilin
* @date 2024/4/22
* @desc
*/
class BaselineGridTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) :
    MaterialTextView(context, attrs, defStyleAttr) {
    private val FOUR_DIP: Float

    private var extraBottomPadding = 0

    private var extraTopPadding = 0

    @FontRes
    private var fontResId = 0

    private var lineHeightHint = 0f

    private var lineHeightMultiplierHint = 1f

    private var maxLinesByHeight = false

    init {
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.BaselineGridTextView, defStyleAttr, 0)

        // first check TextAppearance for line height & font attributes
        if (a.hasValue(R.styleable.BaselineGridTextView_android_textAppearance)) {
            val textAppearanceId =
                a.getResourceId(
                    R.styleable.BaselineGridTextView_android_textAppearance,
                    android.R.style.TextAppearance
                )
            val ta =
                context.obtainStyledAttributes(textAppearanceId, R.styleable.BaselineGridTextView)
            parseTextAttrs(ta)
            ta.recycle()
        }

        // then check view attrs
        parseTextAttrs(a)
        maxLinesByHeight = a.getBoolean(R.styleable.BaselineGridTextView_maxLinesByHeight, false)
        a.recycle()

        FOUR_DIP =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics
            )
        computeLineHeight()
    }

    override fun getCompoundPaddingBottom(): Int {
        // include extra padding to make the height a multiple of 4dp
        return super.getCompoundPaddingBottom() + extraBottomPadding
    }

    override fun getCompoundPaddingTop(): Int {
        // include extra padding to place the first line's baseline on the grid
        return super.getCompoundPaddingTop() + extraTopPadding
    }

    @FontRes fun getFontResId(): Int {
        return fontResId
    }

    fun getLineHeightHint(): Float {
        return lineHeightHint
    }

    fun setLineHeightHint(lineHeightHint: Float) {
        this.lineHeightHint = lineHeightHint
        computeLineHeight()
    }

    fun getLineHeightMultiplierHint(): Float {
        return lineHeightMultiplierHint
    }

    fun setLineHeightMultiplierHint(lineHeightMultiplierHint: Float) {
        this.lineHeightMultiplierHint = lineHeightMultiplierHint
        computeLineHeight()
    }

    fun getMaxLinesByHeight(): Boolean {
        return maxLinesByHeight
    }

    fun setMaxLinesByHeight(maxLinesByHeight: Boolean) {
        this.maxLinesByHeight = maxLinesByHeight
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        extraTopPadding = 0
        extraBottomPadding = 0
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height = measuredHeight
        height += ensureBaselineOnGrid()
        height += ensureHeightGridAligned(height)
        setMeasuredDimension(measuredWidth, height)
        checkMaxLines(height, MeasureSpec.getMode(heightMeasureSpec))
    }

    /**
     * When measured with an exact height, text can be vertically clipped mid-line. Prevent this by
     * setting the `maxLines` property based on the available space.
     */
    private fun checkMaxLines(height: Int, heightMode: Int) {
        if (!maxLinesByHeight || heightMode != MeasureSpec.EXACTLY) {
            return
        }

        val textHeight = height - compoundPaddingTop - compoundPaddingBottom
        val completeLines =
            floor((textHeight / lineHeight).toDouble()).toInt()
        maxLines = completeLines
    }

    /** Ensures line height is a multiple of 4dp.  */
    private fun computeLineHeight() {
        val fm = paint.fontMetrics
        val fontHeight = (abs((fm.ascent - fm.descent).toDouble()) + fm.leading).toFloat()
        val desiredLineHeight =
            if ((lineHeightHint > 0)) lineHeightHint else lineHeightMultiplierHint * fontHeight

        val baselineAlignedLineHeight = ((FOUR_DIP * ceil((desiredLineHeight / FOUR_DIP).toDouble())
            .toFloat()) + 0.5f).toInt()
        setLineSpacing(baselineAlignedLineHeight - fontHeight, 1f)
    }

    /** Ensure that the first line of text sits on the 4dp grid.  */
    private fun ensureBaselineOnGrid(): Int {
        val baseline = baseline.toFloat()
        val gridAlign = baseline % FOUR_DIP
        if (gridAlign != 0f) {
            extraTopPadding = (FOUR_DIP - ceil(gridAlign.toDouble())).toInt()
        }
        return extraTopPadding
    }

    /** Ensure that height is a multiple of 4dp.  */
    private fun ensureHeightGridAligned(height: Int): Int {
        val gridOverhang = height % FOUR_DIP
        if (gridOverhang != 0f) {
            extraBottomPadding = (FOUR_DIP - ceil(gridOverhang.toDouble())).toInt()
        }
        return extraBottomPadding
    }

    private fun parseTextAttrs(a: TypedArray) {
        if (a.hasValue(R.styleable.BaselineGridTextView_lineHeightMultiplierHint)) {
            lineHeightMultiplierHint =
                a.getFloat(R.styleable.BaselineGridTextView_lineHeightMultiplierHint, 1f)
        }
        if (a.hasValue(R.styleable.BaselineGridTextView_lineHeightHint)) {
            lineHeightHint =
                a.getDimensionPixelSize(R.styleable.BaselineGridTextView_lineHeightHint, 0)
                    .toFloat()
        }
        if (a.hasValue(R.styleable.BaselineGridTextView_android_fontFamily)) {
            fontResId = a.getResourceId(R.styleable.BaselineGridTextView_android_fontFamily, 0)
        }
    }
}
