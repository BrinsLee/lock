package com.lock.locksmith.repository.additem

import com.lock.locksmith.bean.AddItemData

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
interface IAddItemRepository {

    fun getItemData(): AddItemData
}