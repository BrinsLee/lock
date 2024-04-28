package com.lock.locksmith.fragments.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination

import com.lock.locksmith.R
import com.lock.locksmith.databinding.FragmentSettingBinding
import com.lock.locksmith.extensions.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */
@AndroidEntryPoint
class SettingFragment: Fragment(R.layout.fragment_setting) {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingBinding.bind(view)
        setupToolbar()
    }

    private fun setupToolbar() {
        val navController: NavController = findNavController(R.id.contentFrame)
        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.topAppBar.title =
                navController.currentDestination?.let { getStringFromDestination(it) }.toString()
        }
    }

    private fun getStringFromDestination(currentDestination: NavDestination): String {
        val idRes = when (currentDestination.id) {
            R.id.mainSettingsFragment -> R.string.Setting

            else -> R.string.app_name
        }
        return getString(idRes)
    }

    companion object {
        val TAG: String = SettingFragment::class.java.simpleName
    }
}