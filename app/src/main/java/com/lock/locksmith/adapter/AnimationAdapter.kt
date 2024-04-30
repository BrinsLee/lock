package com.lock.locksmith.adapter

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import com.chad.library.adapter4.BaseDifferAdapter

import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.lock.locksmith.R
import com.lock.locksmith.model.base.BaseData

/**
 * 文 件 名: AnimationAdapter
 * 创 建 人: Allen
 * 创建日期: 16/12/24 15:33
 * 邮   箱: AllenCoder@126.com
 * 修改时间：
 * 修改备注：
 */
class AnimationAdapter : BaseDifferAdapter<BaseData, QuickViewHolder>(DiffEntityCallback()) {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.list_item_vault, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: BaseData?) {

        item?.let {
            holder.setText(R.id.title, it.itemName)
            holder.setText(R.id.summary, it.accountName)

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