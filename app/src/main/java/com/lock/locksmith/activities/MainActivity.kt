package com.lock.locksmith.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lock.locksmith.R
import com.lock.locksmith.activities.base.AbsBaseActivity
import com.lock.locksmith.databinding.ActivityMainBinding
import com.lock.locksmith.extensions.currentFragment
import com.lock.locksmith.extensions.findNavController
import com.lock.locksmith.extensions.hide
import com.lock.locksmith.extensions.show
import com.lock.locksmith.viewmodel.AddItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AbsBaseActivity() {

    val navigationView get() = binding.navigationView

    val isBottomNavVisible get() = navigationView.isVisible && navigationView is BottomNavigationView

    private val _addItemViewModel: AddItemViewModel by viewModels()
    fun getAddItemViewModel() = _addItemViewModel

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initData()
        initView()
        /*        enableEdgeToEdge()
                setContentView(R.layout.activity_main)
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }*/
    }

    override fun initData() {
        lifecycleScope.launch(Dispatchers.IO) {
            getAddItemViewModel().fetchItemData()
        }
    }

    override fun initView() {
        setupNavigationController()
    }

    private fun setupNavigationController() {
        val navController = findNavController(R.id.nav_host_fragment)
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.main_graph)

        navController.graph = navGraph
        navigationView.setupWithNavController(navController)
        // Scroll Fragment to top
        navigationView.setOnItemReselectedListener {
            currentFragment(R.id.nav_host_fragment).apply {
                /*if (this is IScrollHelper) {
                    scrollToTop()
                }*/
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == navGraph.startDestinationId) {
                currentFragment(R.id.nav_host_fragment)?.enterTransition = null
            }
            when (destination.id) {
                R.id.action_home, R.id.action_setting -> {
                    setBottomNavVisibility(visible = true, animate = true)

                }
                else -> {
                    setBottomNavVisibility(visible = false, animate = true)
                }
            }
        }
    }

    fun setBottomNavVisibility(
        visible: Boolean,
        animate: Boolean = false,
    ) {
        if (!ViewCompat.isLaidOut(navigationView)) {
            return
        }
        if (visible xor navigationView.isVisible) {
            val mAnimate = animate
            if (mAnimate) {
                if (visible) {
                    binding.navigationView.bringToFront()
                    binding.navigationView.show()
                } else {
                    binding.navigationView.hide()
                }
            } else {
                binding.navigationView.isVisible = visible
                if (visible) {
                    binding.navigationView.bringToFront()
                }
            }
        }
    }
}