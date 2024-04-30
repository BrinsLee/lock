package com.lock.locksmith.adapter

import androidx.recyclerview.widget.DiffUtil
import com.lock.locksmith.model.base.BaseData

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
class DiffEntityCallback : DiffUtil.ItemCallback<BaseData>(){
    override fun areItemsTheSame(oldItem: BaseData, newItem: BaseData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BaseData, newItem: BaseData): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}