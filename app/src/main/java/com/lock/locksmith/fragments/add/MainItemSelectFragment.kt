package com.lock.locksmith.fragments.add

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.lock.locksmith.R
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.adapter.GroupAdapter
import com.lock.locksmith.bean.Group
import com.lock.locksmith.databinding.FragmentMainRecyclerBinding
import com.lock.locksmith.extensions.dip
import com.lock.locksmith.fragments.base.AbsRecyclerViewFragment
import com.lock.locksmith.viewmodel.AddItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
class MainItemSelectFragment: AbsRecyclerViewFragment<ConcatAdapter, RecyclerView.LayoutManager, FragmentMainRecyclerBinding>() {

    private lateinit var addItemViewModel: AddItemViewModel

    private val onItemClickListener: BaseQuickAdapter.OnItemClickListener<Group> = object : BaseQuickAdapter.OnItemClickListener<Group> {
        override fun onClick(adapter: BaseQuickAdapter<Group, *>, view: View, position: Int) {
            // when(adapter.items[position].title_res)
            findNavController().navigate(
                R.id.addPasswordFragment,
                null,
                navOptions
            )
        }
    }

    override fun createAdapter(): ConcatAdapter {
        return ConcatAdapter()
    }

    override fun initBinding(view: View): FragmentMainRecyclerBinding {
        return FragmentMainRecyclerBinding.bind(view)
    }

    override fun createLayoutManager(): LayoutManager {
        return LinearLayoutManager(requireContext())
    }

    override fun checkForMargins() {
        if (absBaseActivity is MainActivity && (absBaseActivity as MainActivity).isBottomNavVisible) {
            viewBinding.recyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = dip(R.dimen.bottom_nav_height)
            }
        }
    }

    override fun setupRecyclerView() {
        viewBinding.recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
        }
        addItemViewModel = (absBaseActivity as MainActivity).getAddItemViewModel()
        addItemViewModel.itemData.observe(viewLifecycleOwner) {
            it.forEach {
                val adapter = GroupAdapter()
                adapter.setOnDebouncedItemClick(block = onItemClickListener)
                adapter.submitList(it.group_list)
                // 创建好以后，直接扔进 ConcatAdapter
                mAdapter?.addAdapter(adapter)
            }
        }
    }
}