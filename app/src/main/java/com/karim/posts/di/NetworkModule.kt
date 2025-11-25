package com.karim.posts.di

import android.util.Log
import com.karim.posts.BuildConfig
import com.karim.posts.core.utils.Constants
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor(
        logger = { message ->
            Log.d(Constants.HTTP_LOGGER, message)
        }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideHeadersInterceptor() : Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json").build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor, headersInterceptor: Interceptor
    ) : OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
        addInterceptor(headersInterceptor)
    }.build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient, moshi: Moshi
    ) = Retrofit.Builder().baseUrl(
        BuildConfig.BASE_URL
    ).client(okHttpClient).addConverterFactory(
        MoshiConverterFactory.create(moshi)
    ).build()
}