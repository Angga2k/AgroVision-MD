package com.dicoding.agrovision.data.retrofit

import com.dicoding.agrovision.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/everything")
    suspend fun getTobaccoNews(
        @Query("q") query: String = "tembakau",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("q") query: String = "tembakau",
        @Query("country") country: String = "id",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

}

