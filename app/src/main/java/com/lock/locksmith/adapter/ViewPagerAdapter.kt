package com.lock.locksmith.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author lipeilin
 * @date 2024/4/24
 * @desc
 */
class ViewPagerAdapter(activity: FragmentActivity, var fragments: List<Fragment>): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}