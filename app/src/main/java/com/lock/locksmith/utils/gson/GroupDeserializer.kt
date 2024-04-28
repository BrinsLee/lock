package com.lock.locksmith.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.lock.locksmith.R
import com.lock.locksmith.bean.AddItemData
import com.lock.locksmith.bean.Group
import com.lock.locksmith.utils.StringUtils
import java.lang.reflect.Type

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
class GroupDeserializer:  JsonDeserializer<Group> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Group {
        val jsonObject = json.asJsonObject
        val icon: Int
        val title = when(jsonObject.get("title_res").asString) {
            "password" -> {
                icon = R.drawable.ic_item_password
                StringUtils.getString(R.string.passwords)
            }
            "secure_note" -> {
                icon = R.drawable.ic_item_bank_account
                StringUtils.getString(R.string.secure_notes)
            }
            "contact_info" -> {
                icon = R.drawable.ic_item_bank_account
                StringUtils.getString(R.string.contact_info)
            }
            "payment_card" -> {
                icon = R.drawable.ic_item_bank_account
                StringUtils.getString(R.string.payment_card)
            }
            "bank_account" -> {
                icon = R.drawable.ic_item_bank_account
                StringUtils.getString(R.string.bank_account)
            }
            else -> {
                icon = R.drawable.ic_item_bank_account
                ""
            }
        }

        return Group(icon, title)
    }
}