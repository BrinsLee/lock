package com.lock.locksmith.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.apptheme.helper.ThemeStore
import com.lock.locksmith.R
import com.lock.locksmith.databinding.SearchToolbarLayoutBinding
import com.lock.locksmith.extensions.addAlpha
import com.lock.locksmith.extensions.dp2px
import com.lock.locksmith.extensions.getDimension
import com.lock.locksmith.extensions.throttle
import com.lock.locksmith.utils.DensityUtil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author lipeilin
 * @date 2024/4/23
 * @desc
 */
class SearchToolBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
): RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        val TAG = this::class.java.simpleName
    }

    val searchToolBarLayoutBinding = SearchToolbarLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    val LL_SEARCH_MIN_TOP_MARGIN: Float = context.resources.getDimension(R.dimen.search_layout_top_margin_min)//布局关闭时顶部距离
    val LL_SEARCH_MAX_TOP_MARGIN: Float = context.resources.getDimension(R.dimen.search_layout_top_margin)//布局默认展开时顶部距离
    val LL_SEARCH_TAB_MIN_TOP_MARGIN: Float = context.resources.getDimension(R.dimen.navigator_margin_top_min)
    val LL_SEARCH_TAB_MAX_TOP_MARGIN: Float = context.resources.getDimension(R.dimen.navigator_margin_top)
    val LL_SEARCH_MAX_WIDTH: Float =
        (DensityUtil.getScreenWidth(context) - context.dp2px( 30f)).toFloat()//布局默认展开时的宽度
    val LL_SEARCH_MIN_WIDTH: Float =
        (DensityUtil.getScreenWidth(context) - context.dp2px( 110f)).toFloat();//布局关闭时的宽度
    val TV_TITLE_MAX_TOP_MARGIN: Float = context.dp2px( 20f).toFloat()
    val MAX_TOTAL_SCROLLY = context.getDimension(R.dimen.max_total_scrolly)

    private val searchLayoutParams: MarginLayoutParams = searchToolBarLayoutBinding.searchLlSearch.layoutParams as MarginLayoutParams
    private val searchRootLayoutParams: MarginLayoutParams = searchToolBarLayoutBinding.searchRlTop.layoutParams as MarginLayoutParams
    private val searchTabLayoutParams: MarginLayoutParams = searchToolBarLayoutBinding.magicIndicator.layoutParams as MarginLayoutParams

    private var totalScrollY = 0f


    fun onScrollChanged(dy: Float) {
        Log.d("dydydy", "before computeDy $dy totalScrollY ${totalScrollY}")

        totalScrollY += dy

        if (totalScrollY < 0) totalScrollY = 0f
        totalScrollY = min(MAX_TOTAL_SCROLLY.toFloat(), totalScrollY)
        Log.d("dydydy", "after computeDy $dy totalScrollY ${totalScrollY}")
        Log.d("dydydy", "======================================")

        var searchLayoutNewTopMargin = LL_SEARCH_MAX_TOP_MARGIN - totalScrollY
        var searchTabNewTopMargin = LL_SEARCH_TAB_MAX_TOP_MARGIN - totalScrollY

        var searchLayoutNewWidth = LL_SEARCH_MAX_WIDTH - totalScrollY * 1.3f//此处 * 1.3f 可以设置搜索框宽度缩放的速率

        val titleNewTopMargin: Float =  ((TV_TITLE_MAX_TOP_MARGIN - totalScrollY * 0.5).toFloat());

        searchLayoutNewWidth =
            if (searchLayoutNewWidth < LL_SEARCH_MIN_WIDTH) LL_SEARCH_MIN_WIDTH.toFloat() else searchLayoutNewWidth


        if (searchLayoutNewTopMargin < LL_SEARCH_MIN_TOP_MARGIN) {
            searchLayoutNewTopMargin = LL_SEARCH_MIN_TOP_MARGIN
        }

        if (searchTabNewTopMargin < LL_SEARCH_TAB_MIN_TOP_MARGIN) {
            searchTabNewTopMargin = LL_SEARCH_TAB_MIN_TOP_MARGIN
        }

        if (searchLayoutNewWidth < LL_SEARCH_MIN_WIDTH) {
            searchLayoutNewWidth = LL_SEARCH_MIN_WIDTH
        }

        val ratio = titleNewTopMargin / TV_TITLE_MAX_TOP_MARGIN

        var titleAlpha = 255 * ratio

        if (titleAlpha < 0) {
            titleAlpha = 0f
        }

        //设置相关控件的LayoutParams  此处使用的是MarginLayoutParams，便于设置params的topMargin属性
        searchToolBarLayoutBinding.searchTvTitle.setTextColor(searchToolBarLayoutBinding.searchTvTitle.textColors.withAlpha(
            titleAlpha.roundToInt()
        ))
        searchToolBarLayoutBinding.searchIvAdd.setAlpha(max(0f, ratio))
        searchToolBarLayoutBinding.imageButton.setAlpha(min(1f, 1 - ratio))
        /*        titleLayoutParams.topMargin = titleNewTopMargin.toInt()
                searchToolBarLayoutBinding.searchTvTitle.setLayoutParams(titleLayoutParams)*/
        searchLayoutParams.topMargin = searchLayoutNewTopMargin.roundToInt()
        searchLayoutParams.width = searchLayoutNewWidth.roundToInt()
        searchToolBarLayoutBinding.searchLlSearch.setLayoutParams(searchLayoutParams)

        searchTabLayoutParams.topMargin = searchTabNewTopMargin.roundToInt()
        searchToolBarLayoutBinding.magicIndicator.setLayoutParams(searchTabLayoutParams)



        /*var searchLayoutNewTopMargin = LL_SEARCH_MAX_TOP_MARGIN - dy
        var searchTabNewTopMargin = LL_SEARCH_TAB_MAX_TOP_MARGIN - dy

        var searchLayoutNewWidth = LL_SEARCH_MAX_WIDTH - dy * 1.3f//此处 * 1.3f 可以设置搜索框宽度缩放的速率

        val titleNewTopMargin: Float =  ((TV_TITLE_MAX_TOP_MARGIN - dy * 0.5).toFloat());

        searchLayoutNewWidth =
            if (searchLayoutNewWidth < LL_SEARCH_MIN_WIDTH) LL_SEARCH_MIN_WIDTH.toFloat() else searchLayoutNewWidth


        if (searchLayoutNewTopMargin < LL_SEARCH_MIN_TOP_MARGIN) {
            searchLayoutNewTopMargin = LL_SEARCH_MIN_TOP_MARGIN
        }

        if (searchTabNewTopMargin < LL_SEARCH_TAB_MIN_TOP_MARGIN) {
            searchTabNewTopMargin = LL_SEARCH_TAB_MIN_TOP_MARGIN
        }

        if (searchLayoutNewWidth < LL_SEARCH_MIN_WIDTH) {
            searchLayoutNewWidth = LL_SEARCH_MIN_WIDTH
        }

        val ratio = titleNewTopMargin / TV_TITLE_MAX_TOP_MARGIN

        var titleAlpha = 255 * ratio

        if (titleAlpha < 0) {
            titleAlpha = 0f
        }

        //设置相关控件的LayoutParams  此处使用的是MarginLayoutParams，便于设置params的topMargin属性
        searchToolBarLayoutBinding.searchTvTitle.setTextColor(searchToolBarLayoutBinding.searchTvTitle.textColors.withAlpha(
            titleAlpha.roundToInt()
        ))
        searchToolBarLayoutBinding.searchIvAdd.setAlpha(max(0f, ratio))
        searchToolBarLayoutBinding.imageButton.setAlpha(min(1f, 1 - ratio))
*//*        titleLayoutParams.topMargin = titleNewTopMargin.toInt()
        searchToolBarLayoutBinding.searchTvTitle.setLayoutParams(titleLayoutParams)*//*
        searchLayoutParams.topMargin = searchLayoutNewTopMargin.roundToInt()
        searchLayoutParams.width = searchLayoutNewWidth.roundToInt()
        searchToolBarLayoutBinding.searchLlSearch.setLayoutParams(searchLayoutParams)

        searchTabLayoutParams.topMargin = searchTabNewTopMargin.roundToInt()
        searchToolBarLayoutBinding.magicIndicator.setLayoutParams(searchTabLayoutParams)*/

    }

    init {

        if (!isInEditMode) {
            searchRootLayoutParams.topMargin = StatusBarView.getStatusBarHeight(context.resources)
            // marginLayoutParams.topMargin += StatusBarView.getStatusBarHeight(resources)

            val accentColor = ThemeStore.accentColor(context)
            val drawable: GradientDrawable = searchToolBarLayoutBinding.searchLlSearch.background as GradientDrawable
            drawable.setColor(accentColor.addAlpha(0.12F))

            val imageDrawable: GradientDrawable = searchToolBarLayoutBinding.imageButton.background as GradientDrawable
            imageDrawable.setColor(accentColor)
            // searchToolBarLayoutBinding.imageButton.setColorFilter(accentColor)
        }

    }

    fun setListener(listener: OnClickListener) {
        searchToolBarLayoutBinding.imageButton.setOnClickListener(OnClickListener {
            Log.d(TAG, "setListener: imageButton")
            listener.onClick(it)
        }.throttle())

        searchToolBarLayoutBinding.searchIvAdd.setOnClickListener(OnClickListener {
            Log.d(TAG, "setListener: searchIvAdd")
            listener.onClick(it)
        }.throttle())
    }
}