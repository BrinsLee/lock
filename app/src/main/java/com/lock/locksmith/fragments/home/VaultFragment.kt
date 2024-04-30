package com.lock.locksmith.fragments.home

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.lock.locksmith.R
import com.lock.locksmith.adapter.AnimationAdapter
import com.lock.locksmith.adapter.generateRandomString
import com.lock.locksmith.bean.TabBean
import com.lock.locksmith.databinding.FragmentVaultBinding
import com.lock.locksmith.fragments.base.AbsBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/24
 * @desc
 */
@AndroidEntryPoint
class VaultFragment: AbsBaseFragment(R.layout.fragment_vault) {

    private var _binding: FragmentVaultBinding? = null

    private val binding get() = _binding!!

    private var tabBean: TabBean? = null

    private val emptyDataView: View
        get() {
            val notDataView = layoutInflater.inflate(R.layout.empty_view, FrameLayout(requireContext()), false)
            return notDataView
        }

    private val mAnimationAdapter: AnimationAdapter by lazy {
        AnimationAdapter().apply {
            // 打开 Adapter 的动画
            animationEnable = true
            // 是否是首次显示时候加载动画
            isAnimationFirstOnly = true
            isStateViewEnable = true
            stateView = emptyDataView

        }
    }

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
        setupAdapter()
    }

    private fun setupAdapter() {

        binding.apply {
            recyclerView.adapter = mAnimationAdapter
        }
    }
}