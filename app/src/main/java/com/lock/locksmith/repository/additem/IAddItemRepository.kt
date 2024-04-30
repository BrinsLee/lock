package com.lock.locksmith.repository.additem

import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.model.base.BaseData
import com.lock.result.Result
import java.io.File

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
interface IAddItemRepository {

    fun getItemData(): Result<AddItemData>

    fun saveItemData(itemData: BaseData): Result<File>
}