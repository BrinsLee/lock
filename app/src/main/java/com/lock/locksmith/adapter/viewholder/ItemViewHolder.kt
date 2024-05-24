package com.lock.locksmith.adapter.viewholder

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateLayoutParams
import com.lock.locksmith.R
import com.lock.locksmith.adapter.listitem.ItemListItem.NormalItem
import com.lock.locksmith.adapter.playload.ItemListPayloadDiff
import com.lock.locksmith.databinding.ListItemBackgroundViewBinding
import com.lock.locksmith.databinding.ListItemVaultBinding
import com.lock.locksmith.extensions.context
import com.lock.locksmith.extensions.getDimension
import com.lock.locksmith.extensions.inflater
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.views.ItemListView
import com.lock.locksmith.views.ItemListView.ItemClickListener
import com.lock.locksmith.views.ItemListView.ItemLongClickListener
import com.lock.locksmith.views.ItemListView.SwipeListener
import com.lock.locksmith.views.style.ItemListViewStyle

import androidx.core.view.isVisible
import com.lock.locksmith.databinding.ListItemForegroundViewBinding
import com.lock.locksmith.extensions.isRtlLayout
import com.lock.locksmith.views.style.setTextStyle
import kotlin.math.absoluteValue

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class ItemViewHolder @JvmOverloads constructor(
    parent: ViewGroup,
    private val itemClickListener: ItemClickListener,
    private val itemLongClickListener: ItemLongClickListener,
    private val itemCopyListener: ItemClickListener,
    private val itemDeleteListener: ItemClickListener,
    private val swipeListener: SwipeListener,
    private val style: ItemListViewStyle,
    private val isCopyOptionsVisible: ItemListView.ItemOptionVisibilityPredicate,
    private val isDeleteOptionsVisible: ItemListView.ItemOptionVisibilityPredicate,
    private val getCopyOptionsIcon: ItemListView.ItemOptionIconProvider,
    private val getDeleteOptionsIcon: ItemListView.ItemOptionIconProvider,
    private val binding: ListItemVaultBinding = ListItemVaultBinding.inflate(
        parent.inflater,
        parent,
        false
    )
) : SwipeViewHolder(binding.root) {

    private var optionsCount = 1

    private val menuItemWidth =
        context.getDimension(R.dimen.lock_ui_item_list_item_option_icon_width).toFloat()
    private val optionsMenuWidth
        get() = menuItemWidth * optionsCount

    private lateinit var item: BaseData

    init {
        binding.apply {
            itemView.updateLayoutParams {
                height = style.itemHeight
            }

            itemBackgroundView.apply {
                deleteImageView.setOnClickListener {
                    itemCopyListener.onClick(item)
                    swipeListener.onSwipeCanceled(this@ItemViewHolder, absoluteAdapterPosition)
                }
                deleteImageView.setOnClickListener {
                    itemDeleteListener.onClick(item)
                }

                applyStyle(style)
            }

            itemForegroundView.apply {
                root.setOnClickListener {
                    if (!swiping) {
                        itemClickListener.onClick(item)
                    }
                }
                root.setOnLongClickListener {
                    if (!swiping) {
                        itemLongClickListener.onLongClick(item)
                    } else {
                        true
                    }
                }
                root.doOnNextLayout {
                    setSwipeListener(root, swipeListener)
                }
                applyStyle(style)
            }
        }
    }

    override fun bind(normalItem: NormalItem, diff: ItemListPayloadDiff) {
        this.item = normalItem.item
        configureForeground(diff, normalItem)
        configureBackground()
        listener?.onRestoreSwipePosition(this, absoluteAdapterPosition)
    }

    private fun configureForeground(
        diff: ItemListPayloadDiff,
        normalItem: NormalItem
    ) {
        binding.itemForegroundView.apply {
            diff.run {
                val accountName = normalItem.item.accountName
                if (nameChanged || accountNameChanged) {
                    configureItemNameLabel(normalItem.item.itemName)
                }
                if (accountNameChanged) {
                    configureItemSummaryLabel(normalItem.item.accountName)
                }
            }
        }
    }

    private fun configureBackground() {
        configureBackgroundButtons()
    }

    private fun configureBackgroundButtons() {
        var optionsCount = 0
        binding.itemBackgroundView.copyImageView.apply {
            if (style.copyEnable && isCopyOptionsVisible(item)) {
                isVisible = true
                getCopyOptionsIcon.invoke(item)?.also { setImageDrawable(it) }
                optionsCount++
            } else {
                isVisible = false
            }
        }
        binding.itemBackgroundView.deleteImageView.apply {
            if (style.deleteEnabled && isDeleteOptionsVisible(item)) {
                isVisible = true
                getDeleteOptionsIcon.invoke(item)?.also { setImageDrawable(it) }
                optionsCount++
            } else {
                isVisible = false
            }
        }
        this.optionsCount = optionsCount
    }

    /**
     * 侧滑相关接口回调
     */
    override fun getSwipeView(): View {
        return binding.itemForegroundView.root
    }

    override fun getOpenedX(): Float {
        val isRtl = context.isRtlLayout

        return if (isRtl) optionsMenuWidth else -optionsMenuWidth
    }

    override fun getClosedX(): Float {
        return 0f
    }

    override fun isSwiped(): Boolean {
        val swipeLimit = getOpenedX().absoluteValue / 2
        val swipe = getSwipeView().x.absoluteValue

        return swipe >= swipeLimit
    }


    override fun getSwipeDeltaRange(): ClosedFloatingPointRange<Float> {
        val isRtl = context.isRtlLayout

        return if (isRtl) {
            getClosedX()..getOpenedX()
        } else {
            getOpenedX()..getClosedX()
        }
    }

    override fun isSwipeEnabled(): Boolean {
        return optionsCount > 0 && style.swipeEnabled
    }

    /**
     * ForegroundView Ui 相关
     */

    private fun ListItemForegroundViewBinding.configureItemSummaryLabel(accountName: String) {
        summary.text = accountName
    }

    private fun ListItemForegroundViewBinding.configureItemNameLabel(itemName: String) {
        title.text = itemName
    }

    private fun ListItemForegroundViewBinding.applyStyle(style: ItemListViewStyle) {
        foregroundView.backgroundTintList = ColorStateList.valueOf(style.foregroundLayoutColor)
        foregroundView.updateLayoutParams {
            height = style.itemHeight
        }
        title.setTextStyle(style.itemTitleText)
        summary.setTextStyle(style.accountNameText)
        icon.updateLayoutParams<MarginLayoutParams> {
            marginStart = style.itemMarginStart
        }
        title.updateLayoutParams<MarginLayoutParams> {
            marginStart = style.itemTitleMarginStart
        }
        summary.updateLayoutParams<MarginLayoutParams> {
            marginStart = style.itemTitleMarginStart
        }
    }

    /**
     * BackgroundView Ui 相关
     */

    private fun ListItemBackgroundViewBinding.applyStyle(style: ItemListViewStyle) {
        root.setBackgroundColor(style.backgroundLayoutColor)
        backgroundView.setBackgroundColor(style.backgroundLayoutColor)
        backgroundView.updateLayoutParams {
            height = style.itemHeight
        }
        deleteImageView.setImageDrawable(style.deleteIcon)
        copyImageView.setImageDrawable(style.copyIcon)
    }
}