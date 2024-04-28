package com.lock.locksmith.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author lipeilin
 * @date 2024/4/24
 * @desc
 */
@Parcelize
data class TabBean(var id: String, var title: String) : Parcelable {

}
