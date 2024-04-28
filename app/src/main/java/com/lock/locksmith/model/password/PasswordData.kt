package com.lock.locksmith.model.password

import com.apptheme.helper.utils.VersionUtils
import com.brins.locksmith.AccountItemOuterClass
import com.google.protobuf.ByteString
import com.lock.locksmith.BuildConfig
import com.lock.locksmith.LockSmithApplication
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.utils.encrypt.newAesKey
import com.lock.locksmith.utils.encrypt.newUUID
import java.util.Date

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc
 */
class PasswordData(itemName: String, accountName: String, password: String, note: String) :
    BaseData(itemName, accountName, password, note) {

        init {
            encryptData(password)
        }

    override fun createMetaData() {
        val accountKey = newAesKey().encoded
        val builder: AccountItemOuterClass.AccountItemMeta.Builder =
            AccountItemOuterClass.AccountItemMeta.newBuilder()
                .setSource(AccountItemOuterClass.AccountItemMeta.AccountSource.create)
                .setType(AccountItemOuterClass.AccountItemMeta.AccountType.password)
                .setAccountID(ByteString.copyFrom(newUUID()))
                .setAccountKey(ByteString.copyFrom(accountKey))
                .setCreationDate(Date().time / 1000)
                .setAppVersion(
                    VersionUtils.getAppVersionCode(LockSmithApplication.getContext()).toString()
                )
        metaData = builder.build()
    }
}