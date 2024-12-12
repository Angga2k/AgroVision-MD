package com.dicoding.agrovision.data.retrofit

import com.dicoding.agrovision.data.model.NewsResponse
import com.dicoding.agrovision.data.model.PredictionResponse
import com.dicoding.agrovision.data.model.SavePredictionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("v2/everything")
    suspend fun getTobaccoNews(
        @Query("q") query: String = "pertanian tembakau Indonesia",
        @Query("page") page: Int,
        @Query("language") language: String ="id",
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String,
        @Query("sortBy") sortBy: String = "publishedAt"
    ): Response<NewsResponse>

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("q") query: String = "pertanian tembakau Indonesia",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

    @Multipart
    @POST("prediction")
    suspend fun predictLeaf(
        @Part file: MultipartBody.Part
    ): Response<PredictionResponse>

    @Multipart
    @POST("save-prediction")
    suspend fun savePrediction(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("result") result: RequestBody,
        @Part("accuracy") accuracy: RequestBody
    ): Response<SavePredictionResponse>
}

