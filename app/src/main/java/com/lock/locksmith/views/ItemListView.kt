package com.lock.locksmith.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lock.locksmith.R
import com.lock.locksmith.adapter.listitem.ItemListItem
import com.lock.locksmith.adapter.viewholder.SwipeViewHolder
import com.lock.locksmith.adapter.viewholder.factory.ItemListItemViewHolderFactory
import com.lock.locksmith.extensions.dpToPx
import com.lock.locksmith.extensions.inflater
import com.lock.locksmith.extensions.showToast
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.viewmodel.BaseViewModel
import com.lock.locksmith.viewmodel.BaseViewModel.ErrorEvent.AddItemError
import com.lock.locksmith.viewmodel.BaseViewModel.ErrorEvent.DeleteItemError
import com.lock.locksmith.viewmodel.BaseViewModel.ErrorEvent.InitPassportError
import com.lock.locksmith.viewmodel.BaseViewModel.ErrorEvent.UpdateItemError
import com.lock.locksmith.views.layoutmanager.ScrollPauseLinearLayoutManager
import com.lock.locksmith.views.style.ItemListViewStyle

/**
 * @author lipeilin
 * @date 2024/5/21
 * @desc
 */
class ItemListView : FrameLayout {

    private companion object {
        private val defaultChildLayoutParams: LayoutParams by lazy {
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER,
            )
        }

        private const val KEY_SUPER_STATE = "super_state"
        private const val KEY_SCROLL_STATE = "scroll_state"
    }

    private val ITEM_LIST_VIEW_ID = generateViewId()

    private lateinit var emptyStateView: View

    private lateinit var loadingView: View

    private var itemListItemPredicate: ItemListItemPredicate = ItemListItemPredicate { true }

    private lateinit var simpleItemListView: SimpleItemListView

    private var itemInfoListener: ItemClickListener = ItemClickListener.DEFAULT

    // private var channelLeaveListener: ItemClickListener = ChannelClickListener.DEFAULT

    private var errorEventHandler: ErrorEventHandler = ErrorEventHandler { errorEvent ->
        when (errorEvent) {
            is DeleteItemError -> {
                R.string.delete_item_error
            }

            is AddItemError -> {
                R.string.add_item_error
            }

            is InitPassportError -> {
                R.string.init_passport_error
            }

            is UpdateItemError -> {
                R.string.update_item_error
            }
        }.let(::showToast)

    }

    private lateinit var style: ItemListViewStyle

    private var itemListUpdateListener: ItemListUpdateListener? = ItemListUpdateListener { items ->
        (layoutManager as? ScrollPauseLinearLayoutManager)?.let { layoutManager ->
            if (items.contains(ItemListItem.LoadingMoreItem) &&
                layoutManager.findLastVisibleItemPosition() in items.size - 2..items.size
            ) {
                layoutManager.scrollToPosition(items.size - 1)
            }
        }
    }

    /**
     * The pending scroll state that we need to restore.
     */
    private var layoutManagerState: Parcelable? = null

    /**
     * The layout manager of the inner RecyclerView.
     */
    private val layoutManager: RecyclerView.LayoutManager?
        get() = if (::simpleItemListView.isInitialized) {
            simpleItemListView.layoutManager
        } else {
            null
        }

    constructor(context: android.content.Context) : this(context, null, 0)

    constructor(context: android.content.Context, attrs: android.util.AttributeSet?) : this(
        context,
        attrs,
        0
    )

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        style = ItemListViewStyle.initStyle(context, attrs)

        setBackgroundColor(style.backgroundColor)

        simpleItemListView = SimpleItemListView(context, attrs, defStyleAttr)
            .apply {
                id = ITEM_LIST_VIEW_ID
                setItemListViewStyle(style)
            }
        setItemSeparatorHeight(style.itemVerticalSpacerHeight)
        addView(
            simpleItemListView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
        emptyStateView = inflater.inflate(style.emptyStateView, this, false).apply {
            isVisible = false
            addView(this)
        }

        loadingView = inflater.inflate(style.loadingView, this, false).apply {
            isVisible = false
            addView(this)
        }
        configureDefaultCopyOptionListener(context)
    }

    /**
     * Returns the inner [RecyclerView] that is used to display a list of list items.
     *
     * @return The inner [RecyclerView] with channels.
     */
    fun getRecyclerView(): RecyclerView {
        return simpleItemListView
    }

    /**
     * Returns [LinearLayoutManager] associated with the inner [RecyclerView].
     *
     * @return [LinearLayoutManager] associated with the inner [RecyclerView]
     */
    fun getLayoutManager(): LinearLayoutManager? {
        return layoutManager as? LinearLayoutManager
    }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            KEY_SUPER_STATE to super.onSaveInstanceState(),
            KEY_SCROLL_STATE to layoutManager?.onSaveInstanceState(),
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is Bundle) {
            super.onRestoreInstanceState(state)
            return
        }
        layoutManagerState = state.getParcelable(KEY_SCROLL_STATE)
        super.onRestoreInstanceState(state.getParcelable(KEY_SUPER_STATE))
    }

    /**
     * Restores the scroll state based on the persisted
     */
    private fun restoreLayoutManagerState() {
        if (layoutManagerState != null) {
            layoutManager?.onRestoreInstanceState(layoutManagerState)
            layoutManagerState = null
        }
    }

    /**
     * @return if the list and its adapter are initialized.
     */
    fun isAdapterInitialized(): Boolean {
        return ::simpleItemListView.isInitialized && simpleItemListView.isAdapterInitialized()
    }

    /**
     * @param view Will be added to the view hierarchy of [ChannelListView] and managed by it.
     * This view should not be added to another [ViewGroup] instance elsewhere.
     * @param layoutParams Defines how the view will be situated inside its container ViewGroup.
     */
    @JvmOverloads
    fun setEmptyStateView(view: View, layoutParams: LayoutParams = defaultChildLayoutParams) {
        removeView(this.emptyStateView)
        this.emptyStateView = view
        addView(emptyStateView, layoutParams)
    }

    /**
     * @param view Will be added to the view hierarchy of [ChannelListView] and managed by it.
     * This view should not be added to another [ViewGroup] instance elsewhere.
     * @param layoutParams Defines how the view will be situated inside its container ViewGroup.
     */
    @JvmOverloads
    fun setLoadingView(view: View, layoutParams: LayoutParams = defaultChildLayoutParams) {
        removeView(this.loadingView)
        this.loadingView = view
        addView(loadingView, layoutParams)
    }

    /**
     * Uses the [drawableResource] as the separator for list items.
     *
     * @param drawableResource The drawable used as a separator.
     */
    fun setItemSeparator(@DrawableRes drawableResource: Int) {
        simpleItemListView.setItemSeparator(drawableResource)
    }

    fun setItemSeparatorHeight(dp: Int) {
        simpleItemListView.setItemSeparatorHeight(dp)
    }

    fun setShouldDrawItemSeparatorOnLastItem(shouldDrawOnLastItem: Boolean) {
        simpleItemListView.setShouldDrawItemSeparatorOnLastItem(shouldDrawOnLastItem)
    }

    /**
     * Allows clients to set a custom implementation of [ItemListItemViewHolderFactory].
     *
     * @param factory The custom factory to be used when generating item view holders.
     */
    fun setViewHolderFactory(factory: ItemListItemViewHolderFactory) {
        simpleItemListView.setViewHolderFactory(factory)
    }

    /**
     * Allows clients to set a click listener for all channel list items.
     *
     * @param listener The callback to be invoked on channel item click.
     */
    fun setItemClickListener(listener: ItemClickListener?) {
        simpleItemListView.setItemClickListener(listener)
    }

    /**
     * Allows clients to set a long-click listener for all channel list items.
     *
     * @param listener The callback to be invoked on channel long click.
     */
    fun setItemLongClickListener(listener: ItemLongClickListener?) {
        simpleItemListView.setItemLongClickListener(listener)
    }

    /**
     * Allows clients to set a click listener to be notified of delete clicks via channel actions.
     * or view holder swipe menu
     *
     * @param listener The callback to be invoked when delete is clicked.
     */
    fun setItemDeleteClickListener(listener: ItemClickListener?) {
        simpleItemListView.setItemDeleteClickListener(listener)
    }

    /**
     * Allows clients to set a click listener to be notified of copy clicks in ViewHolder items.
     *
     * @param listener The callback to be invoked when copy is clicked.
     */
    fun setItemCopyClickListener(listener: ItemClickListener?) {
        simpleItemListView.setItemCopyClickListener(listener)
    }

    /**
     * Allows clients to set a visibility controller for the "copy" icon in ViewHolder items.
     *
     * @param isCopyOptionVisible The callback to be invoked when the visibility of "copy" gets checked.
     */
    fun setIsCopyOptionVisible(isCopyOptionVisible: (BaseData) -> Boolean) {
        simpleItemListView.setIsCopyOptionVisible(isCopyOptionVisible)
    }

    /**
     * Allows clients to set a visibility controller for the "delete option" icon in ViewHolder items.
     *
     * @param isDeleteOptionVisible The callback to be invoked when the visibility of "delete option" gets checked.
     */
    fun setIsDeleteOptionVisible(isDeleteOptionVisible: (BaseData) -> Boolean) {
        simpleItemListView.setIsDeleteOptionVisible(isDeleteOptionVisible)
    }

    /**
     * Allows clients to override a "copy options" icon in ViewHolder items.
     *
     * @param getCopyOptionIcon Provides icon for a "copy options".
     */
    fun setCopyOptionIconProvider(getCopyOptionIcon: (BaseData) -> Drawable?) {
        simpleItemListView.setCopyOptionsIconProvider(getCopyOptionIcon)
    }

    /**
     * Allows clients to override a "delete option" icon in ViewHolder items.
     *
     * @param getDeleteOptionIcon Provides icon for delete option.
     */
    fun setDeleteOptionIconProvider(getDeleteOptionIcon: (BaseData) -> Drawable?) {
        simpleItemListView.setDeleteOptionIconProvider(getDeleteOptionIcon)
    }

    /**
     * Allows a client to set a click listener to be notified of "item info".
     *
     * @param listener The callback to be invoked when "item info" is clicked.
     */
    fun setItemInfoClickListener(listener: ItemClickListener?) {
        itemInfoListener = listener ?: ItemClickListener.DEFAULT
    }

    /**
     * Allows a client to set a swipe listener to be notified of swipe details in order to take action.
     *
     * @param listener The set of functions to be invoked during a swipe's lifecycle.
     */
    fun setSwipeListener(listener: SwipeListener?) {
        simpleItemListView.setSwipeListener(listener)
    }

    /**
     * Allows a client to set a listener to be notified of end reached events.
     */
    fun setOnEndReachedListener(listener: EndReachedListener?) {
        simpleItemListView.setOnEndReachedListener(listener)
    }

    /**
     * set the custom scrollChangeListener
     */
    fun setOnScrollChangeListener(listener: RecyclerView.OnScrollListener) {
        simpleItemListView.setScrollChangeListener(listener)
    }

    /**
     * Allow a client to set a listener to be notified when the updated item list is about to be displayed.
     *
     * @param listener The callback to be invoked when the new item list that is about to be displayed.
     */
    fun setItemListUpdateListener(listener: ItemListUpdateListener) {
        itemListUpdateListener = listener
    }

    /**
     * Allows a client to set a ItemListItemPredicate to filter ItemListItems before they are drawn.
     *
     * @param itemListItemPredicate Predicate used to filter the list of ChannelListItem.
     */
    fun setItemListItemPredicate(itemListItemPredicate: ItemListItemPredicate) {
        this.itemListItemPredicate = itemListItemPredicate
        simpleItemListView.currentChannelItemList()?.let(::setItems)
    }

    fun setErrorEventHandler(handler: ErrorEventHandler) {
        this.errorEventHandler = handler
    }

    fun showError(errorEvent: BaseViewModel.ErrorEvent) {
        errorEventHandler.onErrorEvent(errorEvent)
    }

    private fun configureDefaultCopyOptionListener(context: Context) {

    }

    fun setItems(items: List<ItemListItem>) {
        val filteredItems = items.filter(itemListItemPredicate::predicate)

        if (filteredItems.isEmpty()) {
            showEmptyStateView()
        } else {
            hideEmptyStateView()
        }

        simpleItemListView.setItems(filteredItems) {
            restoreLayoutManagerState()
            itemListUpdateListener?.onItemListUpdate(filteredItems)
        }
    }

    public fun hideLoadingView() {
        this.loadingView.isVisible = false
    }

    public fun showLoadingView() {
        hideEmptyStateView()
        this.loadingView.isVisible = true
    }

    private fun showEmptyStateView() {
        this.emptyStateView.isVisible = true
    }

    private fun hideEmptyStateView() {
        this.emptyStateView.isVisible = false
    }

    public fun hasItems(): Boolean {
        return simpleItemListView.hasItems()
    }

    public fun setPaginationEnabled(enabled: Boolean) {
        simpleItemListView.setPaginationEnabled(enabled)
    }

    /**
     * 点击相关接口
     */
    fun interface ItemClickListener {
        companion object {
            @JvmField
            val DEFAULT: ItemClickListener = ItemClickListener {}
        }

        fun onClick(item: BaseData)
    }

    fun interface ItemLongClickListener {
        companion object {
            @JvmField
            val DEFAULT: ItemLongClickListener = ItemLongClickListener {
                // consume the long click by default so that it doesn't become a regular click
                true
            }
        }

        /**
         * Called when a channel has been clicked and held.
         *
         * @return True if the callback consumed the long click, false otherwise.
         */
        fun onLongClick(item: BaseData): Boolean
    }

    /**
     * 错误处理相关
     */
    fun interface ErrorEventHandler {
        fun onErrorEvent(errorEvent: BaseViewModel.ErrorEvent)
    }

    /**
     * Called when the updated list is about to be displayed in the Items [RecyclerView].
     */
    fun interface ItemListUpdateListener {
        /**
         * Called when the updated list is about to be displayed in the Items [RecyclerView].
         *
         * @param Items The new channel list that is about to be displayed.
         */
        fun onItemListUpdate(items: List<ItemListItem>)
    }

    /**
     * 加载更多监听
     */
    fun interface EndReachedListener {
        fun onEndReached()
    }

    /**
     * 侧滑更多操作可见性
     */
    fun interface ItemOptionVisibilityPredicate : Function1<BaseData, Boolean> {
        companion object {
            @JvmField
            val DEFAULT: ItemOptionVisibilityPredicate = ItemOptionVisibilityPredicate {
                // option is visible by default
                true
            }
        }

        /**
         * Called to check option's visibility for the specified [channel].
         *
         * @return True if the option is visible.
         */
        override fun invoke(channel: BaseData): Boolean
    }

    /**
     * 侧滑更多操作图标提供者
     */
    fun interface ItemOptionIconProvider : Function1<BaseData, Drawable?> {

        companion object {
            @JvmField
            val DEFAULT: ItemOptionIconProvider = ItemOptionIconProvider {
                // option has no customized icon by default
                null
            }
        }

        /**
         * Called to provide option's icon for the specified [channel].
         *
         * @return Drawable which overrides ChannelListViewStyle values.
         */
        override fun invoke(channel: BaseData): Drawable?
    }

    /**
     * item 侧滑动监听
     */
    interface SwipeListener {
        /**
         * Invoked when a swipe is detected.
         *
         * @param viewHolder The view holder that is being swiped.
         * @param adapterPosition The internal adapter position of the item being bound.
         * @param x The raw X of the swipe origin; null may indicate the call isn't from user interaction.
         * @param y The raw Y of the swipe origin; null may indicate the call isn't from user interaction.
         */
        fun onSwipeStarted(
            viewHolder: SwipeViewHolder,
            adapterPosition: Int,
            x: Float? = null,
            y: Float? = null
        )

        /**
         * Invoked after a swipe has been detected, and movement is occurring.
         *
         * @param viewHolder The view holder that is being swiped.
         * @param adapterPosition The internal adapter position of the item being bound.
         * @param dX The change from the previous swipe touch event to the current.
         * @param totalDeltaX The change from the first touch event to the current.
         */
        fun onSwipeChanged(
            viewHolder: SwipeViewHolder,
            adapterPosition: Int,
            dX: Float,
            totalDeltaX: Float
        )

        /**
         * Invoked when a swipe is successfully completed naturally, without cancellation.
         *
         * @param viewHolder The view holder that is being swiped.
         * @param adapterPosition The internal adapter position of the item being bound.
         * @param x The raw X of the swipe origin; null may indicate the call isn't from user interaction.
         * @param y The raw Y of the swipe origin; null may indicate the call isn't from user interaction.
         */
        fun onSwipeCompleted(
            viewHolder: SwipeViewHolder,
            adapterPosition: Int,
            x: Float? = null,
            y: Float? = null,
        )

        /**
         * Invoked when a swipe is canceled.
         *
         * @param viewHolder The view holder that is being swiped.
         * @param adapterPosition The internal adapter position of the item being bound.
         * @param x The raw X of the swipe origin; null may indicate the call isn't from user interaction.
         * @param y The raw Y of the swipe origin; null may indicate the call isn't from user interaction.
         */
        fun onSwipeCanceled(
            viewHolder: SwipeViewHolder,
            adapterPosition: Int,
            x: Float? = null,
            y: Float? = null,
        )

        /**
         * Invoked in order to set the [viewHolder]'s initial state when bound. This supports view holder reuse.
         * When items are scrolled off-screen and the view holder is reused, it becomes important to
         * track the swiped state and determine if the view holder should appear as swiped for the item
         * being bound.
         *
         * @param viewHolder The view holder being bound.
         * @param adapterPosition The internal adapter position of the item being bound.
         */
        fun onRestoreSwipePosition(viewHolder: SwipeViewHolder, adapterPosition: Int)

        companion object {
            @JvmField
            val DEFAULT: SwipeListener = object : SwipeListener {
                override fun onSwipeStarted(
                    viewHolder: SwipeViewHolder,
                    adapterPosition: Int,
                    x: Float?,
                    y: Float?,
                ) = Unit

                override fun onSwipeChanged(
                    viewHolder: SwipeViewHolder,
                    adapterPosition: Int,
                    dX: Float,
                    totalDeltaX: Float,
                ) = Unit

                override fun onSwipeCompleted(
                    viewHolder: SwipeViewHolder,
                    adapterPosition: Int,
                    x: Float?,
                    y: Float?,
                ) = Unit

                override fun onSwipeCanceled(
                    viewHolder: SwipeViewHolder,
                    adapterPosition: Int,
                    x: Float?,
                    y: Float?,
                ) = Unit

                override fun onRestoreSwipePosition(
                    viewHolder: SwipeViewHolder,
                    adapterPosition: Int
                ) = Unit
            }
        }
    }

    fun interface ItemListItemPredicate {
        /**
         * Should return true for items that should be kept after filtering.
         */
        fun predicate(channelListItem: ItemListItem): Boolean
    }


}