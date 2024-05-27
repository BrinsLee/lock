package com.lock.locksmith.repository.additem

import com.lock.locksmith.bean.ItemOptionData
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.request.QueryItemsRequest
import com.lock.locksmith.state.QueryItemsState
import com.lock.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
interface IAddItemRepository {

    fun getItemData(): Result<ItemOptionData>

    fun saveItemData(itemData: BaseData): Result<File>

    fun queryItemsAsState(queryItemsRequest: QueryItemsRequest, coroutineScope: CoroutineScope): StateFlow<QueryItemsState?>


}