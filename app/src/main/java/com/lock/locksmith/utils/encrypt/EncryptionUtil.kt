package com.lock.locksmith.utils.encrypt

import android.util.Log
import com.lock.locksmith.model.base.AesEncryptedData
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.spec.ECGenParameterSpec
import java.util.UUID
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc 加解密工具
 */

@Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class)
fun newAesInKeystore(): Cipher {
    return Cipher.getInstance("AES/GCM/NoPadding")
}

/***生成uuid*/
fun newUUID(): ByteArray {
    val uuid = UUID.randomUUID()
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(uuid.mostSignificantBits)
    bb.putLong(uuid.leastSignificantBits)
    return bb.array()
}


/***生成AES256密钥*/
@Throws(NoSuchAlgorithmException::class)
fun newAesKey(keysize: Int = 256): SecretKey {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(keysize)
    return keyGen.generateKey()
}

/***创建密码*/
fun newAesCipher(): Cipher? {
    try {
        return Cipher.getInstance("AES/GCM/NoPadding")
    } catch (e: GeneralSecurityException) {
        Log.e("newAesCipher", "Failed to create AES/GCM/NoPadding cipher", e)
    }

    return null
}


/***生成EC密钥对*/
@Throws(
    RuntimeException::class,
    NoSuchAlgorithmException::class,
    InvalidAlgorithmParameterException::class,
    NoSuchProviderException::class
)
fun generateKeyPair(): KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance("EC")
    val spec = ECGenParameterSpec("prime256v1")
    keyPairGenerator.initialize(spec)
    return keyPairGenerator.generateKeyPair()
}


/***加密相关*/
@Throws(BadPaddingException::class, InvalidKeyException::class, IllegalBlockSizeException::class)
fun aes256Encrypt(key: ByteArray, data: ByteArray): AesEncryptedData {
    val aesKey = SecretKeySpec(key, 0, key.size, "AES")
    return aes256Encrypt(aesKey, data)
}

@Throws(InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class)
private fun aes256Encrypt(key: SecretKey, data: ByteArray): AesEncryptedData {
    val cipher = newAesCipher()!!
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val iv = cipher.iv
    return AesEncryptedData(cipher.doFinal(data), iv)
}




/***解密相关*/

@Throws(
    InvalidAlgorithmParameterException::class,
    InvalidKeyException::class,
    BadPaddingException::class,
    IllegalBlockSizeException::class
)
fun aes256Decrypt(key: ByteArray, encryped: ByteArray, iv: ByteArray): ByteArray {
    val aesKey = SecretKeySpec(key, 0, key.size, "AES")
    return aes256Decrypt(aesKey, encryped, iv)
}

@Throws(
    InvalidAlgorithmParameterException::class,
    InvalidKeyException::class,
    BadPaddingException::class,
    IllegalBlockSizeException::class
)
private fun aes256Decrypt(key: SecretKey, encryped: ByteArray, iv: ByteArray): ByteArray {
    val cipher = newAesCipher()!!
    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
    return cipher.doFinal(encryped)
}
