package com.lock.locksmith

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lock.locksmith.utils.encrypt.aes256Decrypt
import com.lock.locksmith.utils.encrypt.aes256Encrypt
import com.lock.locksmith.utils.encrypt.newAesKey
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author lipeilin
 * @date 2024/4/26
 * @desc
 */
@RunWith(AndroidJUnit4::class)
class EncryptionTest {

    lateinit var context: Context

    val plaintText: String = "123456hahaWUYU你好呀?#@@1#$%^&"

    @Before
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testEncrypt() {
        println("before encrypt: $plaintText")
        val key = newAesKey().encoded
        println("encrypt key: $key")
        val encrypted = aes256Encrypt(
            key,
            plaintText.toByteArray()
        )
        println("after encrypt: ${String(encrypted.bytes)} iv: ${String(encrypted.iv)} tag: ${encrypted.tag}")

        val decrypt = aes256Decrypt(key, encrypted.bytes, encrypted.iv)
        println("after decrypt: ${String(decrypt)}")
    }
}