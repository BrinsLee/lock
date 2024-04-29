package com.lock.locksmith.fragments.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.MenuProvider
import com.apptheme.helper.ThemeStore
import com.lock.locksmith.R
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.databinding.FragmentAddItemBinding
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.viewmodel.AddItemViewModel
import com.lock.locksmith.viewmodel.AddItemViewModel.AddItemEvent.AddPasswordEvent
import kotlin.math.abs

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc
 */
abstract class BaseAddItemFragment(@LayoutRes var childLayout: Int) :
    AbsBaseFragment(R.layout.fragment_add_item) {

    protected lateinit var addItemViewModel: AddItemViewModel

    private var _viewBinding: FragmentAddItemBinding? = null

    protected val viewBinding: FragmentAddItemBinding
        get() {
            return _viewBinding ?: throw IllegalStateException(
                "Should be called initBinding()"
            )
        }

    protected var accentColor: Int = com.theme.helper.R.color.md_deep_purple_A200

    private val menuProvider: MenuProvider = object : MenuProvider {
        private var lastInvokeTime = System.currentTimeMillis()

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.add_item_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            val current = System.currentTimeMillis()
            if (abs(current - lastInvokeTime) < 500) {
                return true
            }
            lastInvokeTime = current
            return onMenuItemClick(menuItem)
        }
    }

    protected open fun onMenuItemClick(menuItem: MenuItem): Boolean {
        return true
    }

    protected open fun saveItemInfo(itemData: BaseData) {
        when(itemData) {
            is PasswordData -> {
                addItemViewModel.addItem(AddPasswordEvent(itemData))
            }
            else -> {
                //TODO
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().addMenuProvider(menuProvider)
        accentColor = ThemeStore.accentColor(requireContext())
        addItemViewModel = (absBaseActivity as MainActivity).getAddItemViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewBinding = FragmentAddItemBinding.bind(view)
        setupChildView()
        setupObserver()
    }

    private fun setupObserver() {
        addItemViewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is AddItemViewModel.State.Loading -> {
                    showLoadingDialog()
                }
                is AddItemViewModel.State.Result -> {
                    dismissLoadingDialog()
                }
            }
        }

        addItemViewModel.errorEvents.observe(viewLifecycleOwner) {
            dismissLoadingDialog()

        }
    }

    protected open fun setupChildView() {
        /*viewBinding.topAppBar.toolbar.apply {
            absBaseActivity?.setSupportActionBar(this)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().removeMenuProvider(menuProvider)
    }
}