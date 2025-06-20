package com.ciwrl.papergram.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import com.ciwrl.papergram.BuildConfig

object RetrofitInstance {

    private const val BASE_URL = "https://export.arxiv.org/api/"

    private val retryInterceptor = Interceptor { chain ->
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null
        var tryCount = 0
        val maxTries = 3

        while (response == null && tryCount < maxTries) {
            tryCount++
            try {
                response = chain.proceed(request)
            } catch (e: IOException) {
                exception = e
                Thread.sleep(1000 * tryCount.toLong())
            }
        }

        if (response == null && exception != null) {
            throw exception
        }

        response!!
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(retryInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .build()
    }

    val arxivApiService: ArxivApiService by lazy {
        retrofit.create(ArxivApiService::class.java)
    }
}