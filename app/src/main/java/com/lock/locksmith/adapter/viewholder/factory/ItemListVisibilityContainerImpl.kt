package com.lock.locksmith.adapter.viewholder.factory

import com.lock.locksmith.utils.ListenerDelegate
import com.lock.locksmith.views.ItemListView.ItemOptionVisibilityPredicate

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
class ItemListVisibilityContainerImpl(
    isCopyOptionsVisible: ItemOptionVisibilityPredicate = copyOptionsDefault,
    isDeleteOptionVisible: ItemOptionVisibilityPredicate = deleteOptionDefault,
): ItemListVisibilityContainer {

    private companion object {
        val copyOptionsDefault: ItemOptionVisibilityPredicate = ItemOptionVisibilityPredicate {
            // "more options" is visible by default
            true
        }

        val deleteOptionDefault: ItemOptionVisibilityPredicate = ItemOptionVisibilityPredicate {
            // "delete option" is visible if the channel's ownCapabilities contains the delete capability
            true
        }
    }

    override var isCopyOptionsVisible: ItemOptionVisibilityPredicate by ListenerDelegate<ItemOptionVisibilityPredicate> (isCopyOptionsVisible) { realListener ->
        ItemOptionVisibilityPredicate { data ->
            realListener().invoke(data)
        }
    }
    override var isDeleteOptionVisible: ItemOptionVisibilityPredicate by ListenerDelegate<ItemOptionVisibilityPredicate> (isDeleteOptionVisible) { realListener ->
        ItemOptionVisibilityPredicate { data ->
            realListener().invoke(data)
        }
    }
}