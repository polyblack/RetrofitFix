package com.polyblack.retrofitfix.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

fun provideApiServiceWithRetrofitFix() =
    RetrofitFix.create(provideRetrofit(), Dispatchers.IO, ApiService::class.java)

fun provideApiService() = provideRetrofit().create(ApiService::class.java)

private fun provideRetrofit() = provideRetrofitBuilder()
    .client(provideOkHttp())
    .build()

private fun provideRetrofitBuilder() = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
    .baseUrl("https://catfact.ninja/")

private fun provideMoshi() = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.d(message)
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    )
    .build()