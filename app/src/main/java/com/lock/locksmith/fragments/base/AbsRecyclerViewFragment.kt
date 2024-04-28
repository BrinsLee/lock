package com.lock.locksmith.fragments.base

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lock.locksmith.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */
abstract class AbsRecyclerViewFragment<A : RecyclerView.Adapter<*>, LM : RecyclerView.LayoutManager, V : ViewBinding>(
    layout: Int = R.layout.fragment_main_recycler
) : AbsBaseFragment(layout) {

    private var _viewBinding: V? = null

    protected val viewBinding: V
        get() {
            return _viewBinding ?: throw IllegalStateException(
                "Should be called initBinding()"
            )
        }

    protected var mAdapter: A? = null
    protected var mLayoutManager: LM? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewBinding = initBinding(view)

        initLayoutManager()
        initAdapter()
        checkForMargins()
        setupRecyclerView()
    }

    open fun setupRecyclerView() {

    }

    private fun initAdapter() {
        mAdapter = createAdapter()
    }

    private fun initLayoutManager() {
        mLayoutManager = createLayoutManager()
    }

    open fun checkForMargins() {

    }

    abstract fun createAdapter(): A

    abstract fun initBinding(view: View): V

    abstract fun createLayoutManager(): LM

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}