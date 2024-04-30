package com.lock.locksmith.extensions

import com.brins.locksmith.AccountItemOuterClass
import com.google.protobuf.ByteString
import com.lock.locksmith.model.base.AesEncryptedData

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */

fun AesEncryptedData.toBuilder(): AccountItemOuterClass.AesEncryptedData.Builder {
    val builder = AccountItemOuterClass.AesEncryptedData.newBuilder()
        .setData(ByteString.copyFrom(bytes))
        .setIv(ByteString.copyFrom(iv))
        if (tag != null) {
            builder.setTag(ByteString.copyFrom(tag))
        }
    return builder

}