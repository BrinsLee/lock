package com.lock.locksmith.repository.passport

import com.lock.result.Result

/**
 * @author lipeilin
 * @date 2024/4/30
 * @desc
 */
interface IPassportRepository {
    fun loadPassport(): Result<Boolean>

    fun createPassport(): Result<Boolean>
}