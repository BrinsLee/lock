package com.lock.locksmith.views.style

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import com.lock.locksmith.R
import com.lock.locksmith.extensions.getColorCompat
import com.lock.locksmith.extensions.getDimension
import com.lock.locksmith.extensions.getDrawableCompat

/**
 * @author lipeilin
 * @date 2024/5/21
 * @desc
 */
data class ItemListViewStyle(
    public val copyIcon: Drawable,
    public val deleteIcon: Drawable,
    public val copyEnable: Boolean,
    public val deleteEnabled: Boolean,
    public val swipeEnabled: Boolean,
    @ColorInt public val backgroundColor: Int,
    @ColorInt public val backgroundLayoutColor: Int,
    public val itemTitleText: TextStyle,
    public val accountNameText: TextStyle,
    public val updateDateText: TextStyle,
    @ColorInt public val foregroundLayoutColor: Int,
    public val itemSeparator: Drawable,
    @LayoutRes public val loadingView: Int,
    @LayoutRes public val emptyStateView: Int,
    @LayoutRes public val loadingMoreView: Int,
    @Px public val itemHeight: Int,
    @Px public val itemMarginStart: Int,
    @Px public val itemMarginEnd: Int,
    @Px public val itemTitleMarginStart: Int,
    @Px public val itemVerticalSpacerHeight: Int,
    @Px public val itemVerticalSpacerPosition: Float,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): ItemListViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ItemListView,
                R.attr.lockUiItemListViewStyle,
                R.style.LockUi_ItemListView
            ).use { a->
                val copyIcon = a.getDrawable(R.styleable.ItemListView_lockUiItemCopyIcon)?: context.getDrawableCompat(R.drawable.lock_ui_ic_copy)!!

                val deleteIcon = a.getDrawable(R.styleable.ItemListView_lockUiItemDeleteIcon)?: context.getDrawableCompat(R.drawable.lock_ui_ic_delete)!!

                val copyEnable = a.getBoolean(R.styleable.ItemListView_lockUiItemCopyEnabled, true)

                val deleteEnabled = a.getBoolean(R.styleable.ItemListView_lockUiItemDeleteEnabled, true)

                val swipeEnabled = a.getBoolean(R.styleable.ItemListView_lockUiSwipeEnabled, true)

                val backgroundColor = a.getColor(R.styleable.ItemListView_lockUiItemListBackgroundColor, context.getColor(R.color.lock_ui_white_smoke))

                val itemTitleText = TextStyle.Builder(a).
                    size(R.styleable.ItemListView_lockUiItemTitleTextSize, context.getDimension(R.dimen.lock_ui_item_item_title)).
                    color(R.styleable.ItemListView_lockUiItemTitleTextColor, context.getColorCompat(
                        com.afollestad.materialdialogs.R.color.md_list_item_textcolor)).
                    style(R.styleable.ItemListView_lockUiItemTitleTextStyle, Typeface.BOLD).build()

                val itemAccountNameText = TextStyle.Builder(a).
                    size(R.styleable.ItemListView_lockUiAccountNameTextSize, context.getDimension(R.dimen.lock_ui_item_item_accountname)).
                    color(R.styleable.ItemListView_lockUiAccountNameTextColor, context.getColorCompat(
                        R.color.grey)).
                    style(R.styleable.ItemListView_lockUiItemTitleTextStyle, Typeface.NORMAL).build()

                val itemUpdateDateText = TextStyle.Builder(a).
                size(R.styleable.ItemListView_lockUiUpdateDateTextSize, context.getDimension(R.dimen.lock_ui_item_item_update_date)).
                color(R.styleable.ItemListView_lockUiUpdateDateTextColor, context.getColorCompat(
                    R.color.grey)).
                style(R.styleable.ItemListView_lockUiUpdateDateTextStyle, Typeface.NORMAL).build()

                val backgroundLayoutColor = a.getColor(
                    R.styleable.ItemListView_lockUiBackgroundLayoutColor,
                    context.getColorCompat(R.color.grey),
                )

                val foregroundLayoutColor = a.getColor(
                    R.styleable.ItemListView_lockUiForegroundLayoutColor,
                    context.getColorCompat(R.color.lock_ui_white_smoke),
                )

                val itemSeparator = a.getDrawable(
                    R.styleable.ItemListView_lockUiItemSeparatorDrawable,
                ) ?: context.getDrawableCompat(R.drawable.lock_ui_divider)!!

                val loadingView = a.getResourceId(
                    R.styleable.ItemListView_lockUiLoadingView,
                    R.layout.default_loading_view,
                )

                val emptyStateView = a.getResourceId(
                    R.styleable.ItemListView_lockUiEmptyStateView,
                    R.layout.empty_view,
                )

                val loadingMoreView = a.getResourceId(
                    R.styleable.ItemListView_lockUiLoadingMoreView,
                    R.layout.default_item_list_loading_more_view,
                )

                val itemHeight = a.getDimensionPixelSize(
                    R.styleable.ItemListView_lockUiItemHeight,
                    context.getDimension(R.dimen.lock_ui_item_list_item_height),
                )

                val itemMarginStart = a.getDimensionPixelSize(
                    R.styleable.ItemListView_lockUiItemMarginStart,
                    context.getDimension(R.dimen.lock_ui_item_list_item_margin_start),
                )

                val itemMarginEnd = a.getDimensionPixelSize(
                    R.styleable.ItemListView_lockUiItemMarginEnd,
                    context.getDimension(R.dimen.lock_ui_item_list_item_margin_end),
                )

                val itemTitleMarginStart = a.getDimensionPixelSize(
                    R.styleable.ItemListView_lockUiItemTitleMarginStart,
                    context.getDimension(R.dimen.lock_ui_item_list_item_title_margin_start),
                )

                val itemVerticalSpacerHeight = a.getDimensionPixelSize(
                    R.styleable.ItemListView_lockUiItemVerticalSpacerHeight,
                    context.getDimension(R.dimen.lock_ui_item_list_item_vertical_spacer_height),
                )

                val itemVerticalSpacerPosition = a.getFloat(
                    R.styleable.ItemListView_lockUiItemVerticalSpacerPosition,
                    ResourcesCompat.getFloat(
                        context.resources,
                        R.dimen.lock_ui_item_list_item_vertical_spacer_position,
                    ),
                )

                return ItemListViewStyle(
                    copyIcon = copyIcon,
                    deleteIcon = deleteIcon,
                    copyEnable = copyEnable,
                    deleteEnabled = deleteEnabled,
                    swipeEnabled = swipeEnabled,
                    backgroundColor = backgroundColor,
                    backgroundLayoutColor = backgroundLayoutColor,
                    itemTitleText = itemTitleText,
                    accountNameText = itemAccountNameText,
                    updateDateText = itemUpdateDateText,
                    foregroundLayoutColor = foregroundLayoutColor,
                    itemSeparator = itemSeparator,
                    loadingView = loadingView,
                    emptyStateView = emptyStateView,
                    loadingMoreView = loadingMoreView,
                    itemHeight = itemHeight,
                    itemMarginStart = itemMarginStart,
                    itemMarginEnd = itemMarginEnd,
                    itemTitleMarginStart = itemTitleMarginStart,
                    itemVerticalSpacerHeight = itemVerticalSpacerHeight,
                    itemVerticalSpacerPosition = itemVerticalSpacerPosition,
                )
            }
        }
    }
}
