package com.bedrest.app.di

import com.bedrest.app.data.remote.AvailabilityApi
import com.bedrest.app.data.repository.AvailabilityRepository
import com.bedrest.app.data.repository.AvailabilityRepositoryImpl
import com.bedrest.app.domain.AvailabilityInteractor
import com.bedrest.app.domain.AvailabilityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun availabilityRepository(@AppModule.AvailabilityClientApi availabilityApi: AvailabilityApi): AvailabilityRepository {
        return AvailabilityRepositoryImpl(availabilityApi)
    }

    @Singleton
    @Provides
    fun availabilityUseCase(@AppModule.AvailabilityClientApi availabilityApi: AvailabilityApi): AvailabilityUseCase {
        return AvailabilityInteractor(availabilityApi)
    }
}