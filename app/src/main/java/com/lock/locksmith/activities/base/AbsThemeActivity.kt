package com.lock.locksmith.activities.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.apptheme.helper.ATHToolbarActivity
import com.apptheme.helper.utils.VersionUtils
import com.lock.locksmith.extensions.exitFullscreen
import com.lock.locksmith.extensions.hideStatusBar
import com.lock.locksmith.extensions.setImmersive
import com.lock.locksmith.extensions.setLightNavigationBarAuto
import com.lock.locksmith.extensions.setLightStatusBarAuto
import com.lock.locksmith.extensions.surfaceColor
import com.lock.locksmith.utils.PreferenceUtil
import com.lock.locksmith.utils.theme.getThemeResValue
import dagger.hilt.android.AndroidEntryPoint

/**
* @author lipeilin
* @date 2024/4/21
* @desc
*/
@AndroidEntryPoint
abstract class AbsThemeActivity : ATHToolbarActivity(), Runnable{

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {

        updateLocale()
        updateTheme()
        hideStatusBar()
        super.onCreate(savedInstanceState)
        setImmersive()

        setLightNavigationBarAuto()
        setLightStatusBarAuto(surfaceColor())

        if (VersionUtils.hasQ()) {
            window.decorView.isForceDarkAllowed = false
        }
    }

    private fun updateLocale() {
        val localeCode = PreferenceUtil.languageCode
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeCode))

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideStatusBar()
            handler.removeCallbacks(this)
            handler.postDelayed(this, 300)
        } else {
            handler.removeCallbacks(this)
        }
    }

    override fun run() {
        setImmersive()
    }

    override fun onStop() {
        handler.removeCallbacks(this)
        super.onStop()
    }


    public override fun onDestroy() {
        super.onDestroy()
        exitFullscreen()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            handler.removeCallbacks(this)
            handler.postDelayed(this, 500)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }


    private fun updateTheme() {
        setTheme(getThemeResValue())
/*        //设置material主题
        if (PreferenceUtil.materialYou) {
            AppCompatDelegate.setDefaultNightMode(getNightMode())
        }
        //使用自定义字体
        if (PreferenceUtil.isCustomFont) {
            setTheme(R.style.FontThemeOverlay)
        }*/
    }

}