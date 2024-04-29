package com.lock.locksmith.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.lock.locksmith.LockFileProvider
import com.lock.result.Error
import com.lock.result.Result
import com.lock.result.Result.Failure
import com.lock.result.Result.Success
import com.lock.result.flatMap
import java.io.File
import java.io.IOException

object FileUtils {
    fun copyFileToUri(context: Context, fromFile: File, toUri: Uri) {
        context.contentResolver.openOutputStream(toUri)
            ?.use { output ->
                fromFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
    }

    /**
     * creates a new file in storage in app specific directory.
     *
     * @return the file
     * @throws IOException
     */
    fun createFile(
        context: Context,
        directoryName: String,
        fileName: String,
        body: String,
        fileType: String
    ): File {
        val root = createDirectory(context, directoryName)
        val filePath = "$root/$fileName$fileType"
        val file = File(filePath)

        // create file if not exist
        if (!file.exists()) {
            try {
                // create a new file and write text in it.
                file.createNewFile()
                file.writeText(body)
                Log.d(FileUtils::class.java.name, "File has been created and saved")
            } catch (e: IOException) {
                Log.d(FileUtils::class.java.name, e.message.toString())
            }
        }
        return file
    }

    /**
     * creates a new directory in storage in app specific directory.
     *
     * @return the file
     */
    private fun createDirectory(context: Context, directoryName: String): File {
        val file = File(
            context.getExternalFilesDir(directoryName)
                .toString()
        )
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }
}

@Suppress("Deprecation")
fun getExternalStorageDirectory(): File {
    return Environment.getExternalStorageDirectory()
}

@Suppress("Deprecation")
fun getExternalStoragePublicDirectory(type: String): File {
    return Environment.getExternalStoragePublicDirectory(type)
}

fun shareStoryToSocial(context: Context, uri: Uri) {
    val feedIntent = Intent(Intent.ACTION_SEND)
    feedIntent.type = "image/*"
    feedIntent.putExtra(Intent.EXTRA_STREAM, uri)
    context.startActivity(feedIntent, null)
}

fun shareFile(context: Context, file: File, mimeType: String) {
    Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(
            Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName,
                file
            )
        )
        context.startActivity(Intent.createChooser(this, null))
    }
}

private const val APP_CACHE_DIR_NAME = "app_cache"
private const val APP_DATA_DIR_NAME = "app_data"




private fun getFileProviderAuthority(context: Context): String {
    val compName = ComponentName(context, LockFileProvider::class.java.name)
    val providerInfo = context.packageManager.getProviderInfo(compName, 0)
    return providerInfo.authority
}


public fun getUriForFile(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, getFileProviderAuthority(context), file)

/**
 * Create the app DATA directory if not exist
 */
private fun getOrCreateAppDataDir(
    context: Context,
): Result<File> {
    return try {
        val file = File(context.dataDir, APP_DATA_DIR_NAME).also { streamCacheDir ->
            streamCacheDir.mkdirs()
        }
        Success(file)
    } catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Could not get or create the App data directory",
                cause = e,
            ),
        )
    }
}

/**
 * Create the file in the app data directory with a given fileName
 *
 */
fun createFileInDataDir(context: Context, fileName: String): Result<File> =
    try {
        getOrCreateAppDataDir(context)
            .flatMap { Success(File(it, fileName)) }
    } catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Could not get or create the file in app data directory.",
                cause = e,
            ),
        )
    }

/**
 * Clear the app data directory
 *
 */
public fun clearAppData(
    context: Context,
): Result<Unit> {
    return try {
        val directory = File(context.dataDir, APP_DATA_DIR_NAME)
        directory.deleteRecursively()

        Success(Unit)
    } catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Could clear the App data directory",
                cause = e,
            ),
        )
    }
}

/**
 * Get the file from the app data directory with a given fileName
 *
 *
 */
public fun getFileFromData(context: Context, fileName: String): Result<Uri> {
    return try {
        when (val getOrCreateDataDirResult = getOrCreateAppDataDir(context)) {
            is Success -> {
                val appDataDir = getOrCreateDataDirResult.value
                val file = File(appDataDir, fileName)
                val isFileCached = file.exists()
                if (isFileCached) {
                    Success(getUriForFile(context, file))
                } else {
                    Failure(Error.GenericError(message = "No such file in cache."))
                }
            }

            is Failure -> {
                getOrCreateDataDirResult
            }
        }
    }catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Cannot determine if the file has been cached.",
                cause = e,
            ),
        )
    }
}








/**
 * Create the app cache directory if not exist
 */
private fun getOrCreateAppCacheDir(
    context: Context,
): Result<File> {
    return try {
        val file = File(context.cacheDir, APP_CACHE_DIR_NAME).also { streamCacheDir ->
            streamCacheDir.mkdirs()
        }
        Success(file)
    } catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Could not get or create the App cache directory",
                cause = e,
            ),
        )
    }
}

/**
 * Create the file in the app cache directory with a given fileName
 */
fun createFileInCacheDir(context: Context, fileName: String): Result<File> =
    try {
        getOrCreateAppCacheDir(context)
            .flatMap { Success(File(it, fileName)) }
    } catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Could not get or create the file in app cache directory.",
                cause = e,
            ),
        )
    }

/**
 *
 * Clear All the content contained in the app cache directory
 */
public fun clearAppCache(
    context: Context,
): Result<Unit> {
    return try {
        val directory = File(context.cacheDir, APP_CACHE_DIR_NAME)
        directory.deleteRecursively()

        Success(Unit)
    } catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Could clear the App cache directory",
                cause = e,
            ),
        )
    }
}

/**
 * Fetches the given file from cache if it has been previously cached.
 */
public fun getFileFromCache(context: Context, fileName: String): Result<Uri> {
    return try {
        when (val getOrCreateCacheDirResult = getOrCreateAppCacheDir(context)) {
            is Success -> {
                val appCacheDir = getOrCreateCacheDirResult.value
                val file = File(appCacheDir, fileName)
                val isFileCached = file.exists()
                if (isFileCached) {
                    Success(getUriForFile(context, file))
                } else {
                    Failure(Error.GenericError(message = "No such file in cache."))
                }
            }

            is Failure -> {
                getOrCreateCacheDirResult
            }
        }
    }catch (e: Exception) {
        Failure(
            Error.ThrowableError(
                message = "Cannot determine if the file has been cached.",
                cause = e,
            ),
        )
    }
}