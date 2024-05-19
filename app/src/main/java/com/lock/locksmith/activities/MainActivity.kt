package com.lock.locksmith.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lock.locksmith.R
import com.lock.locksmith.activities.base.AbsBaseActivity
import com.lock.locksmith.databinding.ActivityMainBinding
import com.lock.locksmith.extensions.currentFragment
import com.lock.locksmith.extensions.dp
import com.lock.locksmith.extensions.findNavController
import com.lock.locksmith.extensions.hide
import com.lock.locksmith.extensions.show
import com.lock.locksmith.extensions.showToast
import com.lock.locksmith.viewmodel.AddItemViewModel
import com.lock.locksmith.viewmodel.PassportViewModel
import com.lock.locksmith.views.BlurMaskLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AbsBaseActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }


    val navigationView get() = binding.navigationView

    val isBottomNavVisible get() = navigationView.isVisible && navigationView is BottomNavigationView

    private val blurMaskLayout: BlurMaskLayout by lazy {
        BlurMaskLayout(this).apply {
            layoutParams =
                ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            elevation = 5.dp.toFloat()
        }
    }

    private val biometricPrompt: BiometricPrompt by lazy {
        BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // user clicked negative button
                    } else {
                        showToast(errString.toString())
                    }
                }

                override fun onAuthenticationSucceeded(result: AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("MAINACTIVITY", "onAuthenticationSucceeded")
                    binding.main.removeView(blurMaskLayout)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                }
            })
    }

    private val promptInfo: BiometricPrompt.PromptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.unlock_with_biometrics))
            .setSubtitle(getString(R.string.unlock_with_fingerprint))
            // .setNegativeButtonText(getString(R.string.cancel))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private val _addItemViewModel: AddItemViewModel by viewModels()
    fun getAddItemViewModel() = _addItemViewModel

    private val _passportViewModel: PassportViewModel by viewModels()
    fun getPassportViewModel() = _passportViewModel

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initData()
        initView()
        Log.d("MAINACTIVITY", "onCreate")

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
            getAddItemViewModel().fetchItemOptionData()
        }
    }

    override fun initView() {
        blurMaskLayout.apply {
            setupWith(binding.main)
            biometricPrompt = this@MainActivity.biometricPrompt
            promptInfo = this@MainActivity.promptInfo
        }
        setupNavigationController()

    }

    override fun onStart() {
        super.onStart()
        Log.d("MAINACTIVITY", "onStart")
        /*if (!binding.main.contains(blurMaskLayout)) {
            binding.main.addView(blurMaskLayout)
        }*/
        // binding.blur.visibility = View.VISIBLE
    }

    private fun setupNavigationController() {

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
                R.id.action_init -> {
                    setBottomNavVisibility(visible = false, animate = false)
                }
                else -> {
                    setBottomNavVisibility(visible = false, animate = true)
                }
            }
        }

        getPassportViewModel().loadPassport().onSuccess {

        }.onError {
            navController.navigate(
                R.id.action_init,
                null
            )
        }
    }

    fun setBottomNavVisibility(
        visible: Boolean,
        animate: Boolean = false,
    ) {
        val mAnimate = (animate && navigationView.isLaidOut)

        if (visible xor navigationView.isVisible) {
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

    override fun onBackPressed() {
        val currentDestination = navController?.currentDestination?.id
        when (currentDestination) {
            R.id.action_home ,
            R.id.action_setting,
            R.id.action_init -> {
                exitApp()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private var mFirstPressTime: Long = 0
    fun exitApp() {
        if ((System.currentTimeMillis() - mFirstPressTime) > 2000) {
            mFirstPressTime = System.currentTimeMillis()
            showToast(R.string.base_exit_main_tip)
        } else {
            finish()
            exitProcess(0)
        }
    }

    fun exitAppNow() {
        finish()
        exitProcess(0)
    }
}