package com.lock.locksmith.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lock.locksmith.adapter.ItemListItemAdapter
import com.lock.locksmith.adapter.decoration.SimpleVerticalListDivider
import com.lock.locksmith.adapter.listitem.ItemListItem
import com.lock.locksmith.adapter.viewholder.factory.ItemListIconProviderContainerImpl
import com.lock.locksmith.adapter.viewholder.factory.ItemListItemViewHolderFactory
import com.lock.locksmith.adapter.viewholder.factory.ItemListListenerContainerImpl
import com.lock.locksmith.adapter.viewholder.factory.ItemListVisibilityContainerImpl
import com.lock.locksmith.adapter.viewholder.factory.ItemSwipeListener
import com.lock.locksmith.extensions.cast
import com.lock.locksmith.extensions.getDrawableCompat
import com.lock.locksmith.views.layoutmanager.ScrollPauseLinearLayoutManager
import com.lock.locksmith.views.style.ItemListViewStyle

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class SimpleItemListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val layoutManager: ScrollPauseLinearLayoutManager
    private val scrollListener: EndReachedScrollListener = EndReachedScrollListener()
    private val dividerDecoration: SimpleVerticalListDivider = SimpleVerticalListDivider(context)

    private lateinit var adapter: ItemListItemAdapter
    private var endReachedListener: ItemListView.EndReachedListener? = null

    private var onScrollChangeListener: OnScrollListener? = null

    internal val listenerContainer = ItemListListenerContainerImpl()

    internal val visibilityContainer = ItemListVisibilityContainerImpl()

    internal val iconProviderContainer = ItemListIconProviderContainerImpl()

    private lateinit var style: ItemListViewStyle

    private lateinit var viewHolderFactory: ItemListItemViewHolderFactory


    init {
        setHasFixedSize(true)
        layoutManager = ScrollPauseLinearLayoutManager(context)
        setLayoutManager(layoutManager)
        setSwipeListener(ItemSwipeListener(this, layoutManager))
        addItemDecoration(dividerDecoration)
    }

    internal fun setItemListViewStyle(style: ItemListViewStyle) {
        this.style = style

        dividerDecoration.drawable = style.itemSeparator
        // style.edgeEffectColor?.let(::setEdgeEffectColor)
    }

    /**
     * @return if the adapter is initialized.
     */
    fun isAdapterInitialized(): Boolean {
        return ::adapter.isInitialized
    }

    private fun requireAdapter(): ItemListItemAdapter {
        if (::adapter.isInitialized.not()) {
            initAdapter()
        }
        return adapter
    }

    private fun initAdapter() {
        if (::viewHolderFactory.isInitialized.not()) {
            viewHolderFactory = ItemListItemViewHolderFactory()
        }
        viewHolderFactory.setListenerContainer(this.listenerContainer)
        viewHolderFactory.setVisibilityContainer(this.visibilityContainer)
        viewHolderFactory.setIconProviderContainer(this.iconProviderContainer)
        viewHolderFactory.setStyle(style)

        adapter = ItemListItemAdapter(viewHolderFactory)

        this.setAdapter(adapter)

        adapter.registerAdapterDataObserver(SnapToTopDataObserver(this))
    }

    public inner class EndReachedScrollListener: OnScrollListener() {
        private var enabled = false
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (SCROLL_STATE_IDLE == newState) {
                val linearLayoutManager = layoutManager?.cast<LinearLayoutManager>()
                val lastVisiblePosition = linearLayoutManager?.findLastVisibleItemPosition()
                val reachedTheEnd = requireAdapter().itemCount - 1 == lastVisiblePosition
                if (reachedTheEnd && enabled) {
                    endReachedListener?.onEndReached()
                }
            }
            // onScrollChangeListener?.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onScrollChangeListener?.onScrolled(recyclerView, dx, dy)
        }

        fun setPaginationEnabled(enabled: Boolean) {
            this.enabled = enabled
        }
    }

    fun setPaginationEnabled(enabled: Boolean) {
        scrollListener.setPaginationEnabled(enabled)
    }


    fun setSwipeListener(listener: ItemListView.SwipeListener?) {
        listenerContainer.swipeListener = listener ?: ItemListView.SwipeListener.DEFAULT
    }

    fun setItemClickListener(listener: ItemListView.ItemClickListener?) {
        listenerContainer.itemClickListener = listener ?: ItemListView.ItemClickListener.DEFAULT
    }

    fun setItemLongClickListener(listener: ItemListView.ItemLongClickListener?) {
        listenerContainer.itemLongClickListener = listener ?: ItemListView.ItemLongClickListener.DEFAULT
    }

    fun setItemDeleteClickListener(listener: ItemListView.ItemClickListener?) {
        listenerContainer.deleteClickListener = listener ?: ItemListView.ItemClickListener.DEFAULT
    }

    fun setItemCopyClickListener(listener: ItemListView.ItemClickListener?) {
        listenerContainer.copyClickListener = listener ?: ItemListView.ItemClickListener.DEFAULT
    }

    fun setIsCopyOptionVisible(isCopyOptionsVisible: ItemListView.ItemOptionVisibilityPredicate) {
        visibilityContainer.isCopyOptionsVisible = isCopyOptionsVisible
    }

    fun setIsDeleteOptionVisible(isDeleteOptionVisible: ItemListView.ItemOptionVisibilityPredicate) {
        visibilityContainer.isDeleteOptionVisible = isDeleteOptionVisible
    }

    fun setCopyOptionsIconProvider(getCopyOptionsIcon: ItemListView.ItemOptionIconProvider) {
        iconProviderContainer.getCopyOptionIcon = getCopyOptionsIcon
    }

    fun setDeleteOptionIconProvider(getDeleteOptionIcon: ItemListView.ItemOptionIconProvider) {
        iconProviderContainer.getDeleteOptionIcon = getDeleteOptionIcon
    }

    fun setItemSeparator(@DrawableRes drawableResource: Int) {
        dividerDecoration.drawable = context.getDrawableCompat(drawableResource)!!
    }

    fun setItemSeparatorHeight(height: Int) {
        dividerDecoration.drawableHeight = height
    }

    fun setShouldDrawItemSeparatorOnLastItem(shouldDrawOnLastItem: Boolean) {
        dividerDecoration.drawOnLastItem = shouldDrawOnLastItem
    }

    fun setOnEndReachedListener(listener: ItemListView.EndReachedListener?) {
        endReachedListener = listener
        observeListEndRegion()
    }

    private fun observeListEndRegion() {
        addOnScrollListener(scrollListener)
    }

    fun setItems(channels: List<ItemListItem>, commitCallback: () -> Unit) {
        requireAdapter().submitList(channels) {
            commitCallback()
        }
    }

    fun hasItems(): Boolean {
        return requireAdapter().itemCount > 0
    }

    // internal fun getItem(cid: String): Channel = adapter.getChannel(cid)


    internal fun currentChannelItemList(): List<ItemListItem>? =
        if (::adapter.isInitialized) adapter.currentList else null

    fun setViewHolderFactory(viewHolderFactory: ItemListItemViewHolderFactory) {
        check(::adapter.isInitialized.not()) { "Adapter was already initialized, please set ItemListItemViewHolderFactory first" }

        this.viewHolderFactory = viewHolderFactory
    }

    override fun onVisibilityChanged(view: View, visibility: Int) {
        super.onVisibilityChanged(view, visibility)
        if (visibility == View.VISIBLE && ::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    fun setScrollChangeListener(listener: OnScrollListener) {
        this.onScrollChangeListener = listener
    }
}