package com.lock.locksmith.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.brins.locksmith.AccountItemOuterClass
import com.lock.locksmith.extensions.toBuilder
import com.lock.locksmith.model.base.AesEncryptedData
import com.lock.locksmith.model.base.BaseData
import com.lock.locksmith.model.filter.FilterObject
import com.lock.locksmith.model.pagination.ItemPaginationRequest
import com.lock.locksmith.model.password.PasswordData
import com.lock.locksmith.model.querysort.QuerySorter
import com.lock.locksmith.model.request.QueryItemsRequest
import com.lock.locksmith.state.QueryItemsMutableState
import com.lock.locksmith.state.QueryItemsState
import com.lock.locksmith.utils.encrypt.aes256Encrypt
import com.lock.locksmith.utils.getOrCreateAppDataDir
import com.lock.locksmith.utils.writeFileInAppDataDir
import com.lock.result.Error
import com.lock.result.Result
import kotlinx.coroutines.CoroutineScope
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lipeilin
 * @date 2024/5/7
 * @desc
 */
@SuppressLint("StaticFieldLeak")
class ItemClient private constructor(val context: Context) {

    private val queryItems: ConcurrentHashMap<Pair<FilterObject, QuerySorter<BaseData>>, QueryItemsMutableState> =
        ConcurrentHashMap()

    companion object {
        val TAG = "ItemClient"

        @Volatile
        private var sInstance: ItemClient? = null

        @JvmStatic
        public fun instance(): ItemClient {
            return sInstance
                ?: throw IllegalStateException(
                    "ItemClient::getInstance must be called before obtaining ItemClient instance",
                )
        }

        fun getInstance(context: Context): ItemClient {
            if (sInstance == null) {
                synchronized(ItemClient::class.java) {
                    if (sInstance == null) {
                        sInstance = ItemClient(context.applicationContext)
                    }
                }
            }
            return sInstance!!
        }

        fun QueryItemsRequest.toPagination(): ItemPaginationRequest =
            ItemPaginationRequest().apply {
                itemLimit = this@toPagination.limit
                itemOffset = this@toPagination.offset
                sort = this@toPagination.querySort
            }
    }

    fun queryItems(
        filter: FilterObject,
        sorter: QuerySorter<BaseData>,
        coroutineScope: CoroutineScope
    ): QueryItemsState {
        return queryItems.getOrPut(filter to sorter) {
            QueryItemsMutableState(filter, sorter, coroutineScope)
        }
    }

    fun fetchItemData(offset: Int, pageSize: Int): Result<List<BaseData>> {
        val dir = getOrCreateAppDataDir(context)
        if (dir.isSuccess) {
            val baseDatas = ArrayList<BaseData>()
            try {
                val value = dir.getOrNull()!!
                if (value.isDirectory) {
                    val filterFiles = value.listFiles()?.filter {
                        it.isFile // 后续加入自定义Filter
                    }?.sortedBy {
                        it.lastModified() // 后续加入自定义排序
                    }?.drop(offset)
                        ?.take(pageSize)
                        ?.map { f ->
                            val item = BaseData.loadFromFile<PasswordData>(f)
                            baseDatas.add(item)
                        }
                }
                return Result.Success(baseDatas)
            } catch (e: Exception) {

            }
        }
        return Result.Failure(Error.GenericError(""))
    }

    fun saveItemData(itemData: BaseData): Result<File> {
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
    }

    /**
     * 加密元数据
     */
    private fun encryptMeta(meta: AccountItemOuterClass.AccountItemMeta?): AesEncryptedData? {
        return PassportClient.instance().encryptData(meta!!.toByteArray())
    }

    /**
     * 加密通用数据
     */
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
            Log.e(PassportClient.TAG, "Failed to encrypt general items", e)
            null
        }
    }
}