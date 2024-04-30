package com.lock.locksmith.fragments.add

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.lock.locksmith.R
import com.lock.locksmith.databinding.FragmentAddPasswordBinding
import com.lock.locksmith.extensions.hideKeyboard
import com.lock.locksmith.fragments.base.BaseAddItemFragment
import com.lock.locksmith.fragments.dialog.ErrorInfoDialogFragment
import com.lock.locksmith.model.password.PasswordData
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc
 */
@AndroidEntryPoint
class AddPasswordFragment : BaseAddItemFragment(R.layout.fragment_add_password) {

    private lateinit var binding: FragmentAddPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextInputLayout()
    }

    override fun setupChildView() {
        super.setupChildView()
        val view = layoutInflater.inflate(childLayout, viewBinding.root, true)
        binding = FragmentAddPasswordBinding.bind(view)
    }

    private fun setupTextInputLayout() {
        binding.itemNameTextContainer.apply {
            // Log.d("taggg", editText?.toString()?:"null")

        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_save -> {
                checkAndSaveInfo()
                return true
            }

            else -> return false
        }
    }


    private fun checkAndSaveInfo() {
        requireContext().hideKeyboard(view)
        if (isValidParams()) {
            saveItemInfo(PasswordData(binding.itemNameTextContainer.editText?.text.toString(),
                binding.userNameTextContainer.editText?.text.toString(),
                binding.passwordTextContainer.editText?.text.toString(),
                binding.noteTextContainer.editText?.text.toString()))
        } else {
            ErrorInfoDialogFragment.create(getString(R.string.missing_info), getMissingInfo()).apply {
                isCancelable = true
            }.show(childFragmentManager, "missPasswordInfo")
        }
    }

    //合法
    private fun isValidParams(): Boolean {
        if (binding.itemNameTextContainer.editText?.text.isNullOrEmpty()) {
            return false
        }
        if (binding.passwordTextContainer.editText?.text.isNullOrEmpty()) {
            return false
        }
        return true
    }


    private fun getMissingInfo(): String {
        return if (binding.itemNameTextContainer.editText?.text.isNullOrEmpty() && binding.passwordTextContainer.editText?.text.isNullOrEmpty()) {
            getString(R.string.please_add_the_item_name) + "\n" + getString(R.string.please_add_the_password)
        } else if (binding.itemNameTextContainer.editText?.text.isNullOrEmpty()) {
            getString(R.string.please_add_the_item_name)
        } else if (binding.passwordTextContainer.editText?.text.isNullOrEmpty()) {
            getString(R.string.please_add_the_password)
        } else {
            ""
        }
    }
}