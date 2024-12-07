package com.dicoding.agrovision.data.retrofit

import com.dicoding.agrovision.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything")
    suspend fun getTobaccoNews(
        @Query("q") query: String = "tobacco",
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>
}

