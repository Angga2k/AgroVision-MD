package com.dicoding.agrovision.data.repository

import com.dicoding.agrovision.data.model.NewsResponse
import com.dicoding.agrovision.data.retrofit.ApiService
import retrofit2.Response

class NewsRepository(val apiService: ApiService) {

    suspend fun fetchTobaccoNews(page: Int, pageSize: Int): Response<NewsResponse> {
        return apiService.getTobaccoNews(
            query = "ekonomi tembakau OR budidaya tembakau OR pasar tembakau OR tantangan petani",
            apiKey = "c1c00ad985ac48d29105fe9a4d52e614",
            page = page,
            pageSize = pageSize
        )
    }
}

