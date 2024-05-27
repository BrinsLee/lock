package com.lock.locksmith.repository.additem

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lock.locksmith.ADD_ITEM_PATH
import com.lock.locksmith.LockSmithApplication
import com.lock.locksmith.bean.ItemOptionData
import com.lock.locksmith.bean.Group
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.pagination.ItemPaginationRequest
import com.lock.locksmith.model.request.QueryItemsRequest
import com.lock.locksmith.repository.ItemClient
import com.lock.locksmith.repository.ItemClient.Companion.toPagination
import com.lock.locksmith.repository.PassportClient
import com.lock.locksmith.state.QueryItemsMutableState
import com.lock.locksmith.state.QueryItemsState
import com.lock.locksmith.utils.StringUtils
import com.lock.locksmith.utils.gson.GroupDeserializer
import com.lock.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bouncycastle.util.encoders.Hex
import java.io.File
import javax.inject.Inject

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
class AddItemRepository @Inject constructor(private val client: PassportClient, private val itemClient: ItemClient): IAddItemRepository {

    companion object{
        const val TAG = "AddItemRepository"


    }

    override fun getItemData(): com.lock.result.Result<ItemOptionData> {
        val inputStream = LockSmithApplication.getContext().applicationContext.assets.open(
            ADD_ITEM_PATH)
        val json: String = StringUtils.getString(inputStream)
        return Result.Success(GsonBuilder().registerTypeAdapter(Group::class.java, GroupDeserializer()).create().fromJson(json, object : TypeToken<ItemOptionData>() {}.type))
    }

    override fun saveItemData(itemData: BaseData): Result<File> {
        return itemClient.saveItemData(itemData)
    }

    override fun queryItemsAsState(queryItemsRequest: QueryItemsRequest, coroutineScope: CoroutineScope): StateFlow<QueryItemsState?> {
        Log.i(TAG, "[queryItemsAsState] dataLimit: ${queryItemsRequest.limit} offset: ${queryItemsRequest.offset}" )

        val queryItemsState: QueryItemsMutableState = itemClient.queryItems(queryItemsRequest.filter, queryItemsRequest.querySort, coroutineScope) as QueryItemsMutableState
        Log.i(TAG, "[queryItemsAsState] QueryItemsMutableState: ${queryItemsState.hashCode()}" )

        queryItemsState.also {
            it.setCurrentRequest(queryItemsRequest)
        }
        val paginationRequest: ItemPaginationRequest = queryItemsRequest.toPagination()
        val stateFlow = MutableStateFlow<QueryItemsState?>(queryItemsState)
        if (queryItemsState.isLoading()) {
            return stateFlow
        }

        val hasOffset = paginationRequest.itemOffset > 0
        loadingPerPage(queryItemsState, true, hasOffset)

        coroutineScope.launch {
            delay(500)
            stateFlow.emit(queryItemsState)
            try {
                val itemsList = itemClient.fetchItemData(paginationRequest.itemOffset, paginationRequest.itemLimit).getOrNull() ?: emptyList()
                val existingItems = queryItemsState.rawItems
                val maps =  itemsList.map { Hex.toHexString(it.getMetaAccountId()) to it}
                queryItemsState.setItems((existingItems ?: emptyMap()) + maps)
                queryItemsState.setEndOfItems(itemsList.size < paginationRequest.itemLimit)
                incrementItemsOffset(queryItemsState, itemsList.size)
            }catch (e: Exception) {
            }
            loadingPerPage(queryItemsState, false, hasOffset)
            stateFlow.emit(queryItemsState)
        }
        return stateFlow
    }

    internal fun incrementItemsOffset(mutableState: QueryItemsMutableState, size: Int) {
        val currentItemsOffset = mutableState.itemsOffset.value
        val newItemsOffset = currentItemsOffset + size
        Log.i(TAG, "[updateItems] newItemsOffset: $newItemsOffset <= $currentItemsOffset" )
        mutableState.setItemsOffset(newItemsOffset)
    }

    private fun loadingPerPage(queryItemsRequest: QueryItemsMutableState, isLoading: Boolean, hasOffset: Boolean) {
        if (hasOffset) {
            Log.i(TAG, "[loadingPerPage] setLoadingMore hasOffset: ${hasOffset} isLoading: ${isLoading}" )
            queryItemsRequest.setLoadingMore(isLoading)
        } else {
            Log.i(TAG, "[loadingPerPage] setLoadingFirstPage hasOffset: ${hasOffset} isLoading: ${isLoading}" )
            queryItemsRequest.setLoadingFirstPage(isLoading)
        }
    }
}


