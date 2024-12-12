package com.dicoding.agrovision.data.repository

import com.dicoding.agrovision.data.model.PredictionResponse
import com.dicoding.agrovision.data.model.SavePredictionResponse
import com.dicoding.agrovision.data.retrofit.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class PredictionRepository {

    private val agroVisionApi = ApiClient.agroVisionApiService

    suspend fun getPrediction(imageFile: File): Response<PredictionResponse> {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

        return agroVisionApi.predictLeaf(filePart)
    }

    suspend fun savePrediction(
        authorization: String,
        file: MultipartBody.Part,
        result: RequestBody,
        accuracy: RequestBody
    ): Response<SavePredictionResponse> {
        return agroVisionApi.savePrediction(authorization, file, result, accuracy)
    }
}