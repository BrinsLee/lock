package com.lock.locksmith.fragments.home

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter.OnTrailingListener
import com.lock.locksmith.R
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.adapter.RecyclerViewAdapter
import com.lock.locksmith.adapter.RecyclerViewAdapter2
import com.lock.locksmith.bean.TabBean
import com.lock.locksmith.databinding.FragmentVaultBinding
import com.lock.locksmith.extensions.binding.bindView
import com.lock.locksmith.extensions.binding.bindView2
import com.lock.locksmith.fragments.base.AbsBaseFragment
import com.lock.locksmith.viewmodel.AddItemViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/24
 * @desc
 */
@AndroidEntryPoint
class VaultFragment : AbsBaseFragment(R.layout.fragment_vault) {

    private lateinit var addItemViewModel: AddItemViewModel

    private var _binding: FragmentVaultBinding? = null

    private val binding get() = _binding!!

    private var tabBean: TabBean? = null

    private val emptyDataView: View
        get() {
            val notDataView =
                layoutInflater.inflate(R.layout.empty_view, FrameLayout(requireContext()), false)
            return notDataView
        }

    private val mAnimationAdapter: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter().apply {
            // 打开 Adapter 的动画
            animationEnable = true
            // 是否是首次显示时候加载动画
            isAnimationFirstOnly = true
            isStateViewEnable = true
            stateView = emptyDataView

        }
    }

    private val adap: RecyclerViewAdapter2 by lazy {
        RecyclerViewAdapter2(emptyList())
    }

    private lateinit var helper: QuickAdapterHelper

    companion object {
        fun newInstance(tabBean: TabBean): VaultFragment {
            return VaultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("tabBean", tabBean)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVaultBinding.bind(view)
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            tabBean = arguments?.getParcelable("tabBean", TabBean::class.java)
        } else {
            tabBean = arguments?.getParcelable("tabBean")
        }
        addItemViewModel = (absBaseActivity as MainActivity).getAddItemViewModel()
        helper = QuickAdapterHelper.Builder(mAnimationAdapter).setTrailingLoadStateAdapter(object :
            OnTrailingListener {
            override fun onLoad() {
                // request()
            }

            override fun onFailRetry() {
                // request()
            }

            override fun isAllowLoading(): Boolean {
                // return !binding.refreshLayout.isRefreshing
                return true
            }
        }).build()
        setupItemList()
    }

    private fun setupItemList() {
        with(binding.refreshLayout) {
            setOnRefreshListener {
                // request()
            }
        }
        with(binding.recyclerView) {
            adapter = helper.adapter
            addItemViewModel.bindView(this, mAnimationAdapter, helper, viewLifecycleOwner)
            /*layoutManager = LinearLayoutManager(requireContext())
            adapter = adap
            addItemViewModel.bindView2(this, adap, viewLifecycleOwner)*/
        }
    }
}