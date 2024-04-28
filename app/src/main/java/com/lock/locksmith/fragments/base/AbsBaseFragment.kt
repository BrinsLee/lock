package com.lock.locksmith.fragments.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.lock.locksmith.R
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.activities.base.AbsBaseActivity
import com.lock.locksmith.fragments.home.HomeFragment
import com.lock.locksmith.fragments.home.HomeFragment.Companion
import com.lock.locksmith.viewmodel.AddItemViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */
@AndroidEntryPoint
open class AbsBaseFragment(@LayoutRes layout: Int): Fragment(layout) {

    var absBaseActivity: MainActivity? = null
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
            absBaseActivity = context as MainActivity?
        } catch (e: ClassCastException) {
            throw RuntimeException(context.javaClass.simpleName + " must be an instance of " + MainActivity::class.java.simpleName)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AbsFragment", "onCreate fragment: ${this::class.java.simpleName} hashcode: ${this}")
    }

    override fun onStart() {
        super.onStart()
        Log.d("AbsFragment", "onStart fragment: ${this::class.java.simpleName} hashcode: ${this}")
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

}