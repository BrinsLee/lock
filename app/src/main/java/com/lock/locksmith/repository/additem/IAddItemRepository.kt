package com.lock.locksmith.repository.additem

import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.model.base.BaseData

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
interface IAddItemRepository {

    fun getItemData(): AddItemData

    fun saveItemData(itemData: BaseData)
}