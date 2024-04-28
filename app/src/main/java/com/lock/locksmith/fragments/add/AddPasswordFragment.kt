package com.lock.locksmith.fragments.add

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import com.lock.locksmith.R
import com.lock.locksmith.databinding.FragmentAddPasswordBinding
import com.lock.locksmith.extensions.addAlpha
import com.lock.locksmith.fragments.base.BaseAddItemFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc
 */
@AndroidEntryPoint
class AddPasswordFragment: BaseAddItemFragment(R.layout.fragment_add_password) {

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
}