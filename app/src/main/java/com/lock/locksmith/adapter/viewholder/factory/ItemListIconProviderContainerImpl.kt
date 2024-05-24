package com.lock.locksmith.adapter.viewholder.factory

import com.lock.locksmith.utils.ListenerDelegate
import com.lock.locksmith.views.ItemListView.ItemOptionIconProvider

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class ItemListIconProviderContainerImpl(
    getCopyOptionsIcon: ItemOptionIconProvider = ItemOptionIconProvider.DEFAULT,
    getDeleteOptionsIcon: ItemOptionIconProvider = ItemOptionIconProvider.DEFAULT
) :
    ItemListIconProviderContainer {
    override var getCopyOptionIcon: ItemOptionIconProvider by ListenerDelegate<ItemOptionIconProvider>(
        getCopyOptionsIcon
    ) { realListener ->
        ItemOptionIconProvider { data ->
            realListener().invoke(data)
        }
    }
    override var getDeleteOptionIcon: ItemOptionIconProvider by ListenerDelegate<ItemOptionIconProvider>(
        getDeleteOptionsIcon
    ) { realListener ->
        ItemOptionIconProvider { data ->
            realListener().invoke(data)
        }
    }
}