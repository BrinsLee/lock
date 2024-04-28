package com.lock.locksmith.fragments.add

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.lock.locksmith.R
import com.lock.locksmith.databinding.FragmentSelectItemBinding
import com.lock.locksmith.fragments.base.AbsBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
@AndroidEntryPoint
class ItemSelectFragment: AbsBaseFragment(R.layout.fragment_select_item) {

    private var _binding: FragmentSelectItemBinding? = null

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectItemBinding.bind(view)
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.topAppBar.toolbar.apply {
            absBaseActivity?.setSupportActionBar(this)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}