package com.lock.locksmith.di

import com.lock.locksmith.repository.PassportClient
import com.lock.locksmith.repository.additem.AddItemRepository
import com.lock.locksmith.repository.additem.IAddItemRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author lipeilin
 * @date 2024/4/25
 * @desc
 */

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun provideItemDataRepository(dataRepositoryImpl: AddItemRepository): IAddItemRepository

}