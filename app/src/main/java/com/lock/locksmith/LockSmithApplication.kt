package com.lock.locksmith

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.apptheme.helper.ThemeStore
import com.lock.locksmith.activities.ErrorActivity
import com.lock.locksmith.activities.MainActivity
import com.lock.locksmith.repository.PassportClient
import dagger.hilt.android.HiltAndroidApp
import java.util.Stack

/**
 * @author lipeilin
 * @date 2024/4/21
 * @desc
 */
@HiltAndroidApp
class LockSmithApplication : Application(), ActivityLifecycleCallbacks {

    companion object {
        private var instance: LockSmithApplication? = null

        fun getContext(): LockSmithApplication {
            return instance!!
        }

        private val mExistedActivity = Stack<Activity>()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initDefaultTheme()
        initErrorActivity()
        initPasswordClient()
        //管理所有的Activity
        registerActivityLifecycleCallbacks(this)
    }

    private fun initPasswordClient() {
        PassportClient.Builder(this).build()
    }

    private fun initErrorActivity() {
        CaocConfig.Builder.create().errorActivity(ErrorActivity::class.java)
            .restartActivity(MainActivity::class.java).apply()
    }

    private fun initDefaultTheme() {
        if (!ThemeStore.isConfigured(this, 3)) {
            ThemeStore.editTheme(this)
                .accentColorRes(com.theme.helper.R.color.md_deep_purple_A200)
                .coloredNavigationBar(true)
                .commit()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        // 把新的 activity 添加到最前面，和系统的 activity 堆栈保持一致
        mExistedActivity.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (mExistedActivity.contains(activity)) {
            mExistedActivity.remove(activity)
        }
    }

    fun currentActivity(): Activity? {
        if (mExistedActivity.empty()) {
            return null
        }
        return mExistedActivity.lastElement()
    }
}