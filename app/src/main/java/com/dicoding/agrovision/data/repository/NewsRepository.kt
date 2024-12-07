package com.dicoding.agrovision.data.repository

import com.dicoding.agrovision.data.model.NewsResponse
import com.dicoding.agrovision.data.retrofit.ApiService
import retrofit2.Response

class NewsRepository(private val apiService: ApiService) {

    suspend fun fetchTobaccoNews(apiKey: String): Response<NewsResponse> {
        return apiService.getTobaccoNews(apiKey = apiKey)
    }
}

