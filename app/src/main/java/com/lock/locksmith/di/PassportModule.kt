package com.lock.locksmith.di

import com.lock.locksmith.repository.ItemClient
import com.lock.locksmith.repository.PassportClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author lipeilin
 * @date 2024/4/29
 * @desc
 */
@Module
@InstallIn(SingletonComponent::class)
object PassportModule {

    @Provides
    @Singleton
    fun providePassportClient() = PassportClient.instance()

    @Provides
    @Singleton
    fun provideItemClient() = ItemClient.instance()
}