package com.lock.locksmith.model.base

import android.util.Log
import com.brins.locksmith.AccountItemOuterClass
import com.brins.locksmith.AccountItemOuterClass.AccountItemMeta
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.model.querysort.ComparableFieldProvider
import com.lock.locksmith.repository.PassportClient
import com.lock.locksmith.utils.encrypt.aes256Decrypt
import com.lock.locksmith.utils.encrypt.aes256Encrypt
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.util.Date
import java.util.Objects
import java.util.Objects.hash
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc
 */

abstract class BaseData(
    var itemName: String = "",
    var accountName: String,
    var password: String,
    var note: String,
) : Serializable, ComparableFieldProvider {

    companion object {
        private val TAG = this::class.java.simpleName
        const val ITEM_NAME_KEY = "ITEM_NAME_KEY"
        const val ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY"
        const val NOTE_KEY = "NOTE_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY"

        @Throws(
            IOException::class,
            InvalidKeyException::class,
            BadPaddingException::class,
            InvalidAlgorithmParameterException::class,
            IllegalBlockSizeException::class
        )
        inline fun <reified T> loadFromFile(file: File): T {
            val fis = FileInputStream(file)
            val item: AccountItemOuterClass.AccountItem =
                AccountItemOuterClass.AccountItem.parseFrom(fis)
            if (item.version != AccountItemOuterClass.AccountItemVersion.accountItemV20240426) {
                throw RuntimeException("unsupported account item version " + item.version)
            }
            when (T::class) {
                PasswordData::class -> {
                    val result = PasswordData("", "", "", "")
                    result.apply {
                        metaData = this.decryptMeta(item)
                        generalItems = this.decryptGeneralItems(item, metaData!!)
                        itemName = generalItems[ITEM_NAME_KEY] ?: ""
                        accountName = generalItems[ACCOUNT_NAME_KEY] ?: ""
                        note = generalItems[NOTE_KEY] ?: ""
                        // password = generalItems[PASSWORD_KEY] ?: ""
                        secretedData = AesEncryptedData(
                            item.secret.data.toByteArray(),
                            item.secret.iv.toByteArray(),
                            item.secret.tag.toByteArray()
                        )
                    }
                    return result as T
                }

                else -> {
                }
            }
            return PasswordData("", "", "", "") as T
        }
    }

    // var bid: String = ""

    /**
     * 加密数据
     */
    var secretedData: AesEncryptedData? = null

    /**
     * 一般通用数据
     * 以键值对存储
     */
    var generalItems: MutableMap<String, String> = HashMap()

    /**
     * 元数据
     */
    var metaData: AccountItemMeta? = null

    /**
     * 创建元数据
     */
    abstract fun createMetaData()


    init {
        if (itemName.isNotEmpty()) {
            // 新增
            this.createMetaData()
            generalItems[ITEM_NAME_KEY] = itemName
            generalItems[ACCOUNT_NAME_KEY] = accountName
            if (note.isNotEmpty()) {
                generalItems[NOTE_KEY] = note
            }
        } else {
            // loadFromFile
        }
    }

    /*    fun encryptMeta(meta: AccountItemOuterClass.AccountItemMeta?): AesEncryptedData? {
            return try {
                repository.encryptData(meta!!.toByteArray())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to encrypt meta", e)
                null
            }

        }*/

    fun encryptData(plainText: String) {
        val secretItems = decryptSecret()
        secretItems?.let {
            it[PASSWORD_KEY] = plainText.toByteArray(StandardCharsets.UTF_8)
            secretedData = encryptSecret(it)
        }
    }

    private fun encryptSecret(secretItems: Map<String, ByteArray>): AesEncryptedData? {
        val builder = AccountItemOuterClass.AccountSecretData.newBuilder()
        for ((key, value) in secretItems) {
            builder.addItems(
                AccountItemOuterClass.SecretItem.newBuilder()
                    .setKey(key)
                    .setValue(ByteString.copyFrom(value))
            )
        }

        val plainText = builder.build().toByteArray()
        if (metaData == null) {
            return null
        }
        return try {
            aes256Encrypt(metaData!!.accountKey.toByteArray(), plainText)
        } catch (e: Exception) {
            Log.e(TAG, "Encrypt secret data failed", e)
            null
        }
    }

    private fun decryptSecret(): MutableMap<String, ByteArray>? {
        val secrets = HashMap<String, ByteArray>()
        if (secretedData == null) {
            return secrets
        }
        try {
            val decrypted = aes256Decrypt(
                metaData!!.accountKey.toByteArray(),
                secretedData!!.bytes,
                secretedData!!.iv
            )
            val secret = AccountItemOuterClass.AccountSecretData.parseFrom(decrypted)
            for (item in secret.itemsList) {
                secrets[item.key] = item.value.toByteArray()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt secret data", e)
        }
        return secrets
    }

    fun getMetaAccountId(): ByteArray = metaData!!.accountID.toByteArray()

    fun getCreateDate(): Long = metaData!!.creationDate

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!(other is BaseData)) return false
        val that: BaseData = other
        return that.metaData?.accountID == this.metaData?.accountID && that.metaData?.accountKey == this.metaData?.accountKey
    }

    override fun hashCode(): Int {
        return hash(this.metaData?.accountID, this.metaData?.accountKey)
    }

    fun areContentsTheSame(newItem: BaseData): Boolean {
        return this.itemName == newItem.itemName &&
            this.accountName == newItem.accountName &&
            this.password == newItem.password &&
            this.note == newItem.note &&
            this.generalItems == newItem.generalItems
    }

    @Throws(
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        InvalidProtocolBufferException::class
    )
    fun decryptMeta(data: AccountItemOuterClass.AccountItem): AccountItemOuterClass.AccountItemMeta? {
        val decrypted: ByteArray =
            PassportClient.instance().decryptData(data.meta.data.toByteArray(), data.meta.iv.toByteArray())
        return AccountItemOuterClass.AccountItemMeta.parseFrom(decrypted)
    }

    @Throws(
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        InvalidProtocolBufferException::class
    )
    fun decryptGeneralItems(
        data: AccountItemOuterClass.AccountItem,
        meta: AccountItemOuterClass.AccountItemMeta
    ): MutableMap<String, String> {
        val decrypted: ByteArray = aes256Decrypt(
            meta.accountKey.toByteArray(),
            data.general.data.toByteArray(),
            data.general.iv.toByteArray()
        )
        val general = AccountItemOuterClass.AccountGeneralData.parseFrom(decrypted)
        val generals: MutableMap<String, String> = java.util.HashMap()
        for (item in general.itemsList) {
            generals[item.key] = item.value
        }
        return generals
    }

    override fun getComparableField(fieldName: String): Comparable<*>? {
        return when (fieldName) {
            "itemName" -> itemName
            "accountName" -> accountName
            "createDate" -> getCreateDate()
            else -> itemName
        }
    }
}