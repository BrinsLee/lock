package com.lock.locksmith.fragments.base

import android.content.Context
import android.content.DialogInterface.OnDismissListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.lock.locksmith.R
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.activities.base.AbsBaseActivity
import com.lock.locksmith.fragments.home.HomeFragment
import com.lock.locksmith.fragments.home.HomeFragment.Companion
import com.lock.locksmith.interfaces.ILoading
import com.lock.locksmith.viewmodel.AddItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */
@AndroidEntryPoint
open class AbsBaseFragment(@LayoutRes layout: Int): Fragment(layout), ILoading {

    var absBaseActivity: AbsBaseActivity? = null
        private set


    val navOptions by lazy {
        navOptions {
            launchSingleTop = false
            anim {
                enter = R.anim.lock_fragment_open_enter
                exit = R.anim.lock_fragment_open_exit
                popEnter = R.anim.lock_fragment_close_enter
                popExit = R.anim.lock_fragment_close_exit
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("AbsFragment", "onAttach fragment: ${this::class.java.simpleName} hashcode: ${this}")

        try {
            absBaseActivity = context as AbsBaseActivity?
        } catch (e: ClassCastException) {
            throw RuntimeException(context.javaClass.simpleName + " must be an instance of " + MainActivity::class.java.simpleName)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AbsFragment", "onCreate fragment: ${this::class.java.simpleName} hashcode: ${this}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("AbsFragment", "onCreateView fragment: ${this::class.java.simpleName} hashcode: ${this}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AbsFragment", "onViewCreated fragment: ${this::class.java.simpleName} hashcode: ${this}")

    }

    override fun onStart() {
        super.onStart()
        Log.d("AbsFragment", "onStart fragment: ${this::class.java.simpleName} hashcode: ${this}")
    }

    override fun onResume() {
        super.onResume()
        Log.d("AbsFragment", "onResume fragment: ${this::class.java.simpleName} hashcode: ${this}")
    }

    override fun onPause() {
        super.onPause()
        Log.d("AbsFragment", "onPause fragment: ${this::class.java.simpleName} hashcode: ${this}")
    }

    override fun onStop() {
        super.onStop()
        Log.d("AbsFragment", "onStop fragment: ${this::class.java.simpleName} hashcode: ${this}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("AbsFragment", "onDestroyView fragment: ${this::class.java.simpleName} hashcode: ${this}")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("AbsFragment", "onDetach fragment: ${this::class.java.simpleName} hashcode: ${this}")
        absBaseActivity = null
    }

    override fun showLoadingDialog() {
        absBaseActivity?.showLoadingDialog()
    }

    override fun showLoadingDialog(listener: OnDismissListener) {
        absBaseActivity?.showLoadingDialog(listener)
    }

    override fun dismissLoadingDialog() {

        absBaseActivity?.dismissLoadingDialog()
    }
}