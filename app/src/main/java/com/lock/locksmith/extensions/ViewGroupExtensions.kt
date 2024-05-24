package com.lock.locksmith.extensions

import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @author lipeilin
 * @date 2024/5/22
 * @desc
 */
public inline val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)