package com.bedrest.app.di

import com.bedrest.app.BuildConfig
import com.bedrest.app.data.remote.ApiClient
import com.bedrest.app.data.remote.AvailabilityApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class AvailabilityClientApi

    @Singleton
    @Provides
    fun createOkHttpClient(): OkHttpClient {
        return ApiClient.createOkHttpClient()
    }

    @Singleton
    @AvailabilityClientApi
    @Provides
    fun createTrendingApi(okHttpClient: OkHttpClient): AvailabilityApi {
        return ApiClient.createApi("", okHttpClient, BuildConfig.BASE_URL)
    }
}