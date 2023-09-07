package com.quypham.assignment.di

import com.quypham.assignment.api.MovieApi
import com.quypham.assignment.api.common.NetworkResultCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    private const val API_TIMEOUT_IN_SECONDS = 60L
    private const val API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0OWI3ODhiYThkYTc1YTg2NDgzODg5OWQ2ZmJkMjkwMiIsInN1YiI6IjY0ZTU3ODgyMWZlYWMxMDBlMTY4Nzc5ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.o07vXyVAVHD-eGFerDjIX_YCNh3dUs1fufBNAAoUfPw"
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    @Singleton
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.addHeader("accept", "application/json")
                builder.addHeader("Authorization", "Bearer $API_KEY")
                chain.proceed(builder.build())
            }
            .readTimeout(API_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(API_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(API_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .build()

        return okHttpClient
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(NetworkResultCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }
}