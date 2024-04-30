package com.lock.locksmith.model.base

import android.util.Log
import com.brins.locksmith.AccountItemOuterClass
import com.brins.locksmith.AccountItemOuterClass.AccountItemMeta
import com.google.protobuf.ByteString
import com.lock.locksmith.utils.encrypt.aes256Decrypt
import com.lock.locksmith.utils.encrypt.aes256Encrypt
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.Objects
import java.util.Objects.hash

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc
 */

abstract class BaseData(
    val itemName: String = "",
    val accountName: String,
    val password: String,
    val note: String,
) : Serializable {

    companion object {
        private val TAG = this::class.java.simpleName
        const val ITEM_NAME_KEY = "ITEM_NAME_KEY"
        const val ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY"
        const val NOTE_KEY = "NOTE_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY"
    }

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
        this.createMetaData()
        generalItems[ITEM_NAME_KEY] = itemName
        generalItems[ACCOUNT_NAME_KEY] = accountName
        if (note.isNotEmpty()) {
            generalItems[NOTE_KEY] = note
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
}