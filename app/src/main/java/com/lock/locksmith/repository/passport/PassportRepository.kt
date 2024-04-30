package com.lock.locksmith.repository.passport

import com.lock.locksmith.repository.PassportClient
import com.lock.result.Error
import com.lock.result.Result
import javax.inject.Inject

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
class PassportRepository @Inject constructor(private val client: PassportClient): IPassportRepository {
    override fun loadPassport(): Result<Boolean> {
        if (client.isPassportValid()) {
            return Result.Success(true)
        } else if (!client.loadDeviceSecretKey()){
            return Result.Failure(Error.GenericError("loadDeviceSecretKey fail"))
        }
        val initResult = client.initPassport()
        return if (initResult) {
            Result.Success(initResult)
        } else {
            Result.Failure(Error.GenericError("initPassport fail"))
        }
    }

    override fun createPassport(): Result<Boolean> {
        val createResult = client.createPassport()
        return if (createResult) {
            Result.Success(createResult)
        } else {
            Result.Failure(Error.GenericError("createPassport fail"))
        }
    }
}