package com.lock.locksmith.repository.additem

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lock.locksmith.ADD_ITEM_PATH
import com.lock.locksmith.LockSmithApplication
import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.bean.Group
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.repository.PassportClient
import com.lock.locksmith.utils.StringUtils
import com.lock.locksmith.utils.gson.GroupDeserializer
import com.lock.result.Result
import java.io.File
import javax.inject.Inject

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
class AddItemRepository @Inject constructor(private val client: PassportClient): IAddItemRepository {


    override fun getItemData(): com.lock.result.Result<AddItemData> {
        val inputStream = LockSmithApplication.getContext().applicationContext.assets.open(
            ADD_ITEM_PATH)
        val json: String = StringUtils.getString(inputStream)
        return Result.Success(GsonBuilder().registerTypeAdapter(Group::class.java, GroupDeserializer()).create().fromJson(json, object : TypeToken<AddItemData>() {}.type))
    }

    override fun saveItemData(itemData: BaseData): Result<File> {
        return client.saveItemData(itemData)
    }
}