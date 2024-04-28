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

import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.lock.locksmith.R

/**
 * 文 件 名: AnimationAdapter
 * 创 建 人: Allen
 * 创建日期: 16/12/24 15:33
 * 邮   箱: AllenCoder@126.com
 * 修改时间：
 * 修改备注：
 */

fun generateRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}
class AnimationAdapter : BaseQuickAdapter<String, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.list_item_vault, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: String?) {

        holder.setText(R.id.tv_title, "Hoteis in Rio de Janeiro")
        val msg =
            "\"He was one of Australia's most of distinguished artistes, renowned for his $item\""



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