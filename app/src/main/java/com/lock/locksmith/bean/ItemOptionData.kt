package com.lock.locksmith.bean

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */

class ItemOptionData : ArrayList<AddItemDataItem>()

data class AddItemDataItem(
    val group_list: List<Group>,
    val group_name: String
)

data class Group(
    val icon_res: Int,
    val title_res: String
)