package com.lock.locksmith.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.apptheme.helper.ThemeStore
import com.apptheme.helper.utils.ColorUtil
import com.lock.locksmith.LockSmithApplication
import com.lock.locksmith.R
import com.lock.locksmith.adapter.ViewPagerAdapter
import com.lock.locksmith.bean.TabBean
import com.lock.locksmith.databinding.FragmentHomeBinding
import com.lock.locksmith.extensions.addAlpha
import com.lock.locksmith.extensions.dp2px
import com.lock.locksmith.fragments.base.AbsBaseFragment
import com.lock.locksmith.views.AnimationNestedScrollView
import com.lock.locksmith.views.StatusBarView
import dagger.hilt.android.AndroidEntryPoint
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */
@AndroidEntryPoint
class HomeFragment : AbsBaseFragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var TABS_TITLE: Array<String> = emptyArray()

    private lateinit var titleList: List<TabBean>

    companion object {
        private const val TAG = ""
    }

    private val fragmentList: ArrayList<AbsBaseFragment> = ArrayList<AbsBaseFragment>()

    private lateinit var fragmentAdapter: ViewPagerAdapter

    private var selectPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TABS_TITLE = arrayOf(
            requireContext().getString(R.string.all_items),
            requireContext().getString(R.string.passwords),
            requireContext().getString(R.string.secure_notes),
            requireContext().getString(R.string.contact_info)
        )

        titleList = TABS_TITLE.toMutableList()
            .mapIndexed { index, string -> TabBean(index.toString(), string) }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupTabLayout()
    }

    private fun setupTabLayout() {
        initTabData()
        initTabAndPager()
    }

    private fun initTabAndPager() {

        fragmentAdapter = ViewPagerAdapter(requireActivity(), fragmentList)
        binding.viewPager.adapter = fragmentAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectPosition = position
            }
        })

        val navigator = CommonNavigator(context).apply {
            val accentColor = ThemeStore.accentColor(context)
            isAdjustMode = false
            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int {
                    return fragmentList.size
                }

                override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                    val titleView = CommonPagerTitleView(context)
                    val customView =
                        LayoutInflater.from(context).inflate(R.layout.include_tab_title, null)
                    val nameView = customView.findViewById<TextView>(R.id.name)
                    val tab = titleList[index]
                    nameView.text = tab.title
                    titleView.setContentView(customView)

                    titleView.onPagerTitleChangeListener =
                        object : CommonPagerTitleView.OnPagerTitleChangeListener {
                            override fun onSelected(index: Int, totalCount: Int) {
                                nameView.setTextColor(
                                    ContextCompat.getColor(
                                        getContext(),
                                        R.color.white
                                    )
                                )
                                nameView.setTypeface(null, Typeface.BOLD)
                            }

                            @SuppressLint("UseCompatLoadingForDrawables")
                            override fun onDeselected(index: Int, totalCount: Int) {
                                nameView.setTextColor(
                                    ColorUtil.resolveColor(
                                        context,
                                        android.R.attr.textColorSecondary
                                    )
                                )
                                nameView.setTypeface(null, Typeface.NORMAL)
                            }

                            override fun onLeave(
                                index: Int,
                                totalCount: Int,
                                leavePercent: Float,
                                leftToRight: Boolean
                            ) {
                            }

                            override fun onEnter(
                                index: Int,
                                totalCount: Int,
                                enterPercent: Float,
                                leftToRight: Boolean
                            ) {
                            }
                        }
                    titleView.setOnClickListener {
                        binding.viewPager.setCurrentItem(index, false)
                    }
                    return titleView
                }

                override fun getIndicator(context: Context?): IPagerIndicator {
                    return WrapPagerIndicator(context).apply {
                        roundRadius = dp2px(4f).toFloat()
                        fillColor = accentColor.addAlpha(0.5F)
                        horizontalPadding = 0
                        verticalPadding = 0
                        horizontalOffset = 4
                        verticalOffset = 0
                    }
                }
            }
        }
        binding.searchRlTop.searchToolBarLayoutBinding.magicIndicator.navigator = navigator
        ViewPagerHelper.bind(
            binding.searchRlTop.searchToolBarLayoutBinding.magicIndicator,
            binding.viewPager
        )
    }

    private fun initTabData() {
        if (fragmentList.isEmpty()) {
            titleList.forEach {
                fragmentList.add(VaultFragment.newInstance(it))
            }
            try {
                binding.viewPager.offscreenPageLimit = titleList.size
            } catch (ignore: Throwable) {
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            searchRlTop.setListener(object : OnClickListener {
                override fun onClick(v: View) {
                    findNavController().navigate(
                        R.id.action_add_item,
                        null,
                        navOptions
                    )
                }
            })
            searchSvView.listener =
                object : AnimationNestedScrollView.OnAnimationScrollChangeListener {
                    override fun onScrollChanged(dy: Float) {
                        binding.searchRlTop.onScrollChanged(dy)
                    }
                }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}