package com.lock.locksmith.fragments.init

import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apptheme.helper.ThemeStore
import com.lock.locksmith.R
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.databinding.FragmentInitPassportBinding
import com.lock.locksmith.fragments.base.AbsBaseFragment
import com.lock.locksmith.fragments.dialog.ErrorInfoDialogFragment
import com.lock.locksmith.viewmodel.AddItemViewModel
import com.lock.locksmith.viewmodel.BaseViewModel.State
import com.lock.locksmith.viewmodel.BaseViewModel.State.Failure
import com.lock.locksmith.viewmodel.BaseViewModel.State.Loading
import com.lock.locksmith.viewmodel.BaseViewModel.State.Result
import com.lock.locksmith.viewmodel.PassportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
@AndroidEntryPoint
class InitPassportFragment: AbsBaseFragment(R.layout.fragment_init_passport) {

    private lateinit var passportViewModel: PassportViewModel


    private var _binding: FragmentInitPassportBinding? = null

    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInitPassportBinding.bind(view)
        binding.ivComplete.apply {
            val imageDrawable: GradientDrawable = background as GradientDrawable
            imageDrawable.setColor(ThemeStore.accentColor(requireContext()))
            setOnClickListener {
                val navController = findNavController()
                navController.popBackStack()
            }
        }
        setupObserver()
        initPassport()
/*        binding.t.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack() // 弹出当前 Fragment，返回到上一个 Fragment

        }*/

    }

    private fun initPassport() {
        passportViewModel.handlerEvent(PassportViewModel.PassportEvent.InitPassportEvent)

    }

    private fun setupObserver() {
        passportViewModel = (absBaseActivity as MainActivity).getPassportViewModel()
        passportViewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is Loading -> {
                    showLoadingDialog()
                }
                is Result -> {
                    dismissLoadingDialog()
                    handleInitSuccess()
                }

                is Failure -> {
                    dismissLoadingDialog()
                }
            }
        }

        passportViewModel.errorEvents.observe(viewLifecycleOwner) {
            val dialogFragment = ErrorInfoDialogFragment.create(getString(R.string.init_passport_error), getString(R.string.init_passport_error_detail))
            dialogFragment.dialogDismissListener = object : ErrorInfoDialogFragment.DialogDismissListener {
                override fun onDialogDismissed() {
                    (absBaseActivity as MainActivity).exitAppNow()
                }
            }
            dialogFragment.show(childFragmentManager, "error")
        }
    }

    private fun handleInitSuccess() {
        binding.subTitle.setText(R.string.init_passport_success)
        binding.ivComplete.visibility = View.VISIBLE
    }
}