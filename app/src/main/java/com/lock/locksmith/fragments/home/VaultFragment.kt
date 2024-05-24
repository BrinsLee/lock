package com.lock.locksmith.fragments.home

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lock.locksmith.R
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.bean.TabBean
import com.lock.locksmith.databinding.FragmentVaultBinding
import com.lock.locksmith.extensions.binding.bindView
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

   /* private val emptyDataView: View
        get() {
            val notDataView =
                layoutInflater.inflate(R.layout.empty_view, FrameLayout(requireContext()), false)
            return notDataView
        }*/


    companion object {
        fun newInstance(tabBean: TabBean): VaultFragment {
            return VaultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("tabBean", tabBean)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabData()
        setupItemList()
    }

    private fun setupTabData() {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            tabBean = arguments?.getParcelable("tabBean", TabBean::class.java)
        } else {
            tabBean = arguments?.getParcelable("tabBean")
        }
        addItemViewModel = (absBaseActivity as MainActivity).getAddItemViewModel()
    }

    private fun setupItemList() {
        /*if (tabBean?.id == "1") {
            return
        }*/
        with(binding.refreshLayout) {
            setOnRefreshListener {
                // request()
            }
        }
        with(binding.recyclerView) {
            addItemViewModel.bindView(this, viewLifecycleOwner)
            /*layoutManager = LinearLayoutManager(requireContext())
            adapter = adap
            addItemViewModel.bindView2(this, adap, viewLifecycleOwner)*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}