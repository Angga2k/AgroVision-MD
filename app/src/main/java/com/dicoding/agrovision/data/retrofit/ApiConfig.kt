package com.dicoding.agrovision.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL_NEWS = "https://newsapi.org/"
    private const val BASE_URL_AGROVISION = "https://agrovision-cc-94017305982.asia-southeast2.run.app/"

    // Retrofit instance untuk News API
    private val retrofitNews: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_NEWS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitAgroVision: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AGROVISION)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val newsApiService: ApiService by lazy {
        retrofitNews.create(ApiService::class.java)
    }

    val agroVisionApiService: ApiService by lazy {
        retrofitAgroVision.create(ApiService::class.java)
    }
}
