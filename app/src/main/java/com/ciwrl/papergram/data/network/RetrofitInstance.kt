package com.ciwrl.papergram.data.network

import com.ciwrl.papergram.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://export.arxiv.org/api/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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
    private const val SEMANTIC_SCHOLAR_BASE_URL = "https://api.semanticscholar.org/graph/v1/"
    private val semanticScholarRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SEMANTIC_SCHOLAR_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) 
            .build()
    }
    val semanticScholarApiService: SemanticScholarApiService by lazy {
        semanticScholarRetrofit.create(SemanticScholarApiService::class.java)
    }
}