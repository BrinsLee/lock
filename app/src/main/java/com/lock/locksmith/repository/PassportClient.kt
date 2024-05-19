package com.lock.locksmith.repository

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Log
import com.brins.locksmith.AccountItemOuterClass
import com.lock.locksmith.DEVICEID_DATA_KEY
import com.lock.locksmith.DEVICEID_IV_KEY
import com.lock.locksmith.DEVICE_PRIVATE_DATA_KEY
import com.lock.locksmith.DEVICE_PRIVATE_IV_KEY
import com.lock.locksmith.DEVICE_PUBLIC_DATA_KEY
import com.lock.locksmith.DEVICE_PUBLIC_IV_KEY
import com.lock.locksmith.MASTER_IV_KEY
import com.lock.locksmith.USERID_DATA_KEY
import com.lock.locksmith.USERID_IV_KEY
import com.lock.locksmith.MASTER_DATA_KEY
import com.lock.locksmith.PASSPORT_PRIVATE_DATA_KEY
import com.lock.locksmith.PASSPORT_PRIVATE_IV_KEY
import com.lock.locksmith.PASSPORT_PUBLIC_DATA_KEY
import com.lock.locksmith.PASSPORT_PUBLIC_IV_KEY
import com.lock.locksmith.extensions.toBuilder
import com.lock.locksmith.model.base.AesEncryptedData
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.scope.ClientScope
import com.lock.locksmith.utils.SpUtils
import com.lock.locksmith.utils.encrypt.aes256Encrypt
import com.lock.locksmith.utils.encrypt.generateKeyPair
import com.lock.locksmith.utils.encrypt.newAesCipher
import com.lock.locksmith.utils.encrypt.newAesInKeystore
import com.lock.locksmith.utils.encrypt.newAesKey
import com.lock.locksmith.utils.encrypt.newUUID
import com.lock.locksmith.utils.getOrCreateAppDataDir
import com.lock.locksmith.utils.writeFileInAppDataDir
import com.lock.result.Error
import com.lock.result.Result
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author lipeilin
 * @date 2024/4/28
 * @desc
 */
class PassportClient(val context: Context, val coroutineScope: ClientScope) {

    /***主密钥*/
    private var _masterSecretKey: SecretKey? = null
    private val masterSecretKey: SecretKey
        get() = _masterSecretKey!!

    /***设备密钥*/
    private var _deviceSecretKey: SecretKey? = null
    private val deviceSecretKey: SecretKey
        get() = _deviceSecretKey!!

/*    fun saveItemData(itemData: BaseData): Result<File> {
        val fileName = Hex.toHexString(itemData.getMetaAccountId())
        val encryptedMeta = encryptMeta(itemData.metaData)
        val encryptedGeneral = encryptGeneral(itemData.metaData!!, itemData.generalItems)
        assert(encryptedMeta != null)
        assert(encryptedGeneral != null)
        val builder = AccountItemOuterClass.AccountItem.newBuilder()
            .setVersion(AccountItemOuterClass.AccountItemVersion.accountItemV20240426)
            .setMeta(encryptedMeta!!.toBuilder())
            .setGeneral(encryptedGeneral!!.toBuilder())
            .setSecret(itemData.secretedData!!.toBuilder())
        return writeFileInAppDataDir(context, fileName, builder.build().toByteArray())
    }*/

/*    fun fetchItemData(pageSize: Int): Result<List<BaseData>> {
        val dir = getOrCreateAppDataDir(context)
        if (dir.isSuccess) {
            val baseDatas = ArrayList<BaseData>()
            try {
                val value = dir.getOrNull()!!
                if (value.isDirectory) {
                    val filterFiles = value.listFiles()?.filter {
                        it.isFile // 后续加入自定义Filter
                    }?.sortedBy {
                        it.name // 后续加入自定义排序
                    }?.take(pageSize)?.map { f ->
                        val data =BaseData.loadFromFile<PasswordData>(f)
                        baseDatas.add(data)
                    }
                }
                return Result.Success(baseDatas)
            } catch (e: Exception) {

            }
        }
        return Result.Failure(Error.GenericError(""))
    }*/

    /**
     * create passport
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class
    )
    fun createPassport(): Boolean {
        if (!createHardwareDeviceKey()) {
            return false
        }

        /***生成UserUUId*/
        userID = newUUID()
        _masterSecretKey = newAesKey()
        val passportKeyPair = generateKeyPair()

        /***生成deviceUUid*/
        deviceID = newUUID()
        val deviceKeyPair = generateKeyPair()

        /***加密UserUUid*/
        val encryptedUserID = encryptInKeystore(userID!!)

        /***加密主密钥*/
        val encryptedMasterKey = encryptInKeystore(masterSecretKey.encoded)

        /*** 加密公钥和私钥**/
        val encryptedPassportPublicKey = encryptInKeystore(passportKeyPair.public.encoded)
        val encryptedPassportPrivateKey = encryptInKeystore(passportKeyPair.private.encoded)
        val encryptedDeviceID = encryptInKeystore(deviceID!!)
        val encryptedDevicePublicKey = encryptInKeystore(deviceKeyPair.public.encoded)
        val encryptedDevicePrivateKey = encryptInKeystore(deviceKeyPair.private.encoded)

        /***Uid*/
        SpUtils.obtain(PassportPreferenceName)
            .save(USERID_IV_KEY, Hex.toHexString(encryptedUserID.iv))
        SpUtils.obtain(PassportPreferenceName)
            .save(USERID_DATA_KEY, Hex.toHexString(encryptedUserID.bytes))
        /***密钥*/
        SpUtils.obtain(PassportPreferenceName)
            .save(MASTER_IV_KEY, Hex.toHexString(encryptedMasterKey.iv))
        SpUtils.obtain(PassportPreferenceName)
            .save(MASTER_DATA_KEY, Hex.toHexString(encryptedMasterKey.bytes))
        /***User密钥对公钥*/
        SpUtils.obtain(PassportPreferenceName)
            .save(PASSPORT_PUBLIC_IV_KEY, Hex.toHexString(encryptedPassportPublicKey.iv))
        SpUtils.obtain(PassportPreferenceName)
            .save(PASSPORT_PUBLIC_DATA_KEY, Hex.toHexString(encryptedPassportPublicKey.bytes))
        /***User密钥对私钥*/
        SpUtils.obtain(PassportPreferenceName)
            .save(PASSPORT_PRIVATE_IV_KEY, Hex.toHexString(encryptedPassportPrivateKey.iv))
        SpUtils.obtain(PassportPreferenceName)
            .save(PASSPORT_PRIVATE_DATA_KEY, Hex.toHexString(encryptedPassportPrivateKey.bytes))
        /***DeviceId*/
        SpUtils.obtain(PassportPreferenceName)
            .save(DEVICEID_IV_KEY, Hex.toHexString(encryptedDeviceID.iv))
        SpUtils.obtain(PassportPreferenceName)
            .save(DEVICEID_DATA_KEY, Hex.toHexString(encryptedDeviceID.bytes))
        /***Device公钥*/
        SpUtils.obtain(PassportPreferenceName)
            .save(DEVICE_PUBLIC_IV_KEY, Hex.toHexString(encryptedDevicePublicKey.iv))
        SpUtils.obtain(PassportPreferenceName)
            .save(DEVICE_PUBLIC_DATA_KEY, Hex.toHexString(encryptedDevicePublicKey.bytes))
        /***Device私钥*/
        SpUtils.obtain(PassportPreferenceName)
            .save(DEVICE_PRIVATE_IV_KEY, Hex.toHexString(encryptedDevicePrivateKey.iv))
        SpUtils.obtain(PassportPreferenceName)
            .save(DEVICE_PRIVATE_DATA_KEY, Hex.toHexString(encryptedDevicePrivateKey.bytes))
        return true
    }

    /**
     * init passport
     */
    fun initPassport(): Boolean {
        return try {
            userID = decryptFromPreference(
                SpUtils.obtain(PassportPreferenceName).getString(USERID_IV_KEY, ""),
                SpUtils.obtain(PassportPreferenceName).getString(USERID_DATA_KEY, "")
            )
            val masterKey = decryptFromPreference(
                SpUtils.obtain(PassportPreferenceName).getString(
                    MASTER_IV_KEY, ""
                ),
                SpUtils.obtain(PassportPreferenceName).getString(MASTER_DATA_KEY, "")
            )
            _masterSecretKey = SecretKeySpec(masterKey, "AES")
            deviceID = decryptFromPreference(
                SpUtils.obtain(PassportPreferenceName).getString(
                    DEVICEID_IV_KEY, ""
                ),
                SpUtils.obtain(PassportPreferenceName).getString(DEVICEID_DATA_KEY, "")
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * decrypt from preference
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun decryptFromPreference(ivString: String, dataString: String): ByteArray? {
        val iv = Hex.decode(ivString)
        val data = Hex.decode(dataString)
        return decryptInKeystore(data, iv)
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    private fun encryptInKeystore(data: ByteArray): AesEncryptedData {
        val cipher = newAesInKeystore()
        cipher.init(Cipher.ENCRYPT_MODE, deviceSecretKey)
        return AesEncryptedData(cipher.doFinal(data), cipher.iv)
    }

    /***解密*/
    private fun decryptInKeystore(data: ByteArray?, iv: ByteArray?): ByteArray? {
        require(!(data == null || iv == null)) { "data or iv must not be null" }
        val cipher = newAesInKeystore()
        cipher.init(Cipher.DECRYPT_MODE, deviceSecretKey, GCMParameterSpec(128, iv))
        return cipher.doFinal(data)
    }

    /***判断密钥是否存在硬件模块中*/
    private fun createHardwareDeviceKey(): Boolean {
        return try {
            /***创建密钥生成器*/
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KeyStoreProvider)

            /***配置密钥生成器参数*/
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                DeviceKeyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()
            keyGenerator.init(keyGenParameterSpec)
            /***生成设备密钥*/
            _deviceSecretKey = keyGenerator.generateKey()

            val factory = SecretKeyFactory.getInstance(deviceSecretKey.algorithm, KeyStoreProvider)
            val keyInfo = factory.getKeySpec(deviceSecretKey, KeyInfo::class.java) as KeyInfo
            /***判断设备是否支持硬件安全模块*/
            if (VERSION.SDK_INT >= VERSION_CODES.S) {
                keyInfo.securityLevel == KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT || keyInfo.securityLevel == KeyProperties.SECURITY_LEVEL_STRONGBOX
            } else {
                keyInfo.isInsideSecureHardware
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * return true if the passport is valid
     */
    fun isPassportValid(): Boolean {
        if (_deviceSecretKey == null) {
            return false
        }

        if (userID == null) {
            return false
        }

        if (_masterSecretKey == null) {
            return false
        }

        if (deviceID == null) {
            return false
        }
        return true
    }

    /**
     * load device secret key
     */
    fun loadDeviceSecretKey(): Boolean {
        try {
            val keyStore = KeyStore.getInstance(KeyStoreProvider)
            keyStore.load(null)
            val secretKeyEntry =
                keyStore.getEntry(DeviceKeyAlias, null) as? KeyStore.SecretKeyEntry ?: return false
            _deviceSecretKey = secretKeyEntry.secretKey
            return true
        } catch (e: CertificateException) {
            Log.e("Passport", "Failed to load device secret key", e)
            return false
        } catch (e: NoSuchAlgorithmException) {
            Log.e("Passport", "Failed to load device secret key", e)
            return false
        } catch (e: IOException) {
            Log.e("Passport", "Failed to load device secret key", e)
            return false
        } catch (e: UnrecoverableEntryException) {
            Log.e("Passport", "Failed to load device secret key", e)
            return false
        } catch (e: KeyStoreException) {
            Log.e("Passport", "Failed to load device secret key", e)
            return false
        }
    }

    companion object {

        private val PassportPreferenceName = "passport"
        private var userID: ByteArray? = null
        private var deviceID: ByteArray? = null
        private val KeyStoreProvider = "AndroidKeyStore"
        private val DeviceKeyAlias = "com.brins.locksmith.locksmith"

        private var instance: PassportClient? = null

        val TAG = "PassportClient"

        @JvmStatic
        public fun instance(): PassportClient {
            return instance
                ?: throw IllegalStateException(
                    "PassportClient.Builder::build() must be called before obtaining PassportClient instance",
                )
        }
    }

    public class Builder(private val context: Context) {

        fun build(): PassportClient {
            val clientScope = ClientScope()

            return PassportClient(context, clientScope).apply {
                instance = this
            }
        }
    }

/*    *//**
     * 加密元数据
     *//*
    private fun encryptMeta(meta: AccountItemOuterClass.AccountItemMeta?): AesEncryptedData? {
        return try {
            encryptData(meta!!.toByteArray())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt meta", e)
            null
        }
    }

    *//**
     * 加密通用数据
     *//*
    private fun encryptGeneral(
        meta: AccountItemOuterClass.AccountItemMeta,
        generalItems: MutableMap<String, String>
    ): AesEncryptedData? {
        val builder = AccountItemOuterClass.AccountGeneralData.newBuilder()
        for (entry in generalItems.entries) {
            builder.addItems(
                AccountItemOuterClass.GeneralItem.newBuilder()
                    .setKey(entry.key)
                    .setValue(entry.value)
            )
        }
        val plainText = builder.build().toByteArray()
        return try {
            aes256Encrypt(meta.accountKey.toByteArray(), plainText)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt general items", e)
            null
        }
    }*/

    @Throws(
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    internal fun encryptData(data: ByteArray): AesEncryptedData {
        val cipher = newAesCipher()!!
        cipher.init(Cipher.ENCRYPT_MODE, masterSecretKey)
        val iv = cipher.iv
        return AesEncryptedData(cipher.doFinal(data), iv)
    }

    @Throws(
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    internal fun decryptData(data: ByteArray, iv: ByteArray): ByteArray {
        val cipher = newAesCipher()!!
        Log.d("masterSecretKey value", Hex.toHexString(masterSecretKey!!.encoded))
        cipher.init(Cipher.DECRYPT_MODE, masterSecretKey, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}