package com.lock.locksmith.repository.additem

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lock.locksmith.ADD_ITEM_PATH
import com.lock.locksmith.LockSmithApplication
import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.bean.Group
import com.lock.locksmith.utils.StringUtils
import com.lock.locksmith.utils.gson.GroupDeserializer
import javax.inject.Inject

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
class AddItemRepository @Inject constructor(): IAddItemRepository {


    override fun getItemData(): AddItemData {
        val inputStream = LockSmithApplication.getContext().applicationContext.assets.open(
            ADD_ITEM_PATH)
        val json: String = StringUtils.getString(inputStream)
        return GsonBuilder().registerTypeAdapter(Group::class.java, GroupDeserializer()).create().fromJson(json, object : TypeToken<AddItemData>() {}.type)
    }
}