package com.lock.locksmith.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.BaseQuickAdapter

import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.lock.locksmith.R
import com.lock.locksmith.databinding.ListItemVaultBinding
import com.lock.locksmith.databinding.ListItemViewBinding
import com.lock.locksmith.model.base.BaseData

/**
 * 文 件 名: AnimationAdapter
 * 创 建 人: Allen
 * 创建日期: 16/12/24 15:33
 * 邮   箱: AllenCoder@126.com
 * 修改时间：
 * 修改备注：
 */
class RecyclerViewAdapter : BaseQuickAdapter<BaseData, RecyclerViewAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val viewBinding: ListItemVaultBinding = ListItemVaultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: BaseData?) {

        item?.let {
            holder.viewBinding.title.text = it.itemName
            holder.viewBinding.summary.text = it.accountName

        }

/*        holder.getView<TextView>(R.id.tweetText).text = buildSpannedString {
            append(msg)
            inSpans(clickableSpan) {
                append("landscapes and nedes")
            }
        }
        holder.getView<TextView>(R.id.tweetText).movementMethod = ClickableMovementMethod.getInstance()
        holder.getView<TextView>(R.id.tweetText).isFocusable = false
        holder.getView<TextView>(R.id.tweetText).isClickable = false
        holder.getView<TextView>(R.id.tweetText).isLongClickable = false*/
    }

}