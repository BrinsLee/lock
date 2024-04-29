package com.lock.locksmith.adapter

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.apptheme.helper.ThemeStore
import com.chad.library.adapter4.BaseQuickAdapter
import com.lock.locksmith.R
import com.lock.locksmith.bean.Group
import com.lock.locksmith.databinding.ListItemGroupTypeBinding
import com.lock.locksmith.extensions.dp
import com.lock.locksmith.extensions.getTintedDrawable

/**
 * 每一组的Adapter
 *
 */
class GroupAdapter : BaseQuickAdapter<Group, GroupAdapter.VH>(){

    class VH(
        parent: ViewGroup,
        val binding: ListItemGroupTypeBinding = ListItemGroupTypeBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
    ):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Group?) {
        if (item == null) return

        holder.binding.tvTitle.text = item.title_res

        /*val drawable: Drawable? = ContextCompat.getDrawable(context, item.icon_res)
        val colorFilter: ColorFilter = PorterDuffColorFilter(
            ThemeStore.accentColor(context),
            SRC_IN
        )
        drawable?.mutate()?.setColorFilter(colorFilter)*/
        val drawable = context.getTintedDrawable(item.icon_res, ThemeStore.accentColor(context))

        holder.binding.icon.setImageDrawable(drawable)

        holder.binding.lineView.isVisible = position == items.lastIndex


    }
}