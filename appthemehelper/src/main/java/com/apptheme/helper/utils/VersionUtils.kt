package com.apptheme.helper.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * @author Hemanth S (h4h13).
 */

object VersionUtils {

    fun getAppVersionCode(context: Context): Long {
        val packageManager: PackageManager = context.getPackageManager()
        return try {
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(context.getPackageName(), 0)
            val versionCode = packageInfo.versionCode.toLong()
            versionCode
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }


    /**
     * @return true if device is running API >= 23
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
    fun hasMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * @return true if device is running API >= 24
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
    fun hasNougat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    /**
     * @return true if device is running API >= 25
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
    fun hasNougatMR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    }

    /**
     * @return true if device is running API >= 26
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun hasOreo(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    /**
     * @return true if device is running API >= 27
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
    fun hasOreoMR1(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    }

    /**
     * @return true if device is running API >= 28
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    fun hasP(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    /**
     * @return true if device is running API >= 29
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    @JvmStatic
    fun hasQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    /**
     * @return true if device is running API >= 30
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    @JvmStatic
    fun hasR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    /**
     * @return true if device is running API >= 31
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    @JvmStatic
    fun hasS(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    /**
     * @return true if device is running API >= 33
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    @JvmStatic
    fun hasT(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}