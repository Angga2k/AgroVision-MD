package com.dicoding.agrovision.data.repository

import com.dicoding.agrovision.data.model.PredictionResponse
import com.dicoding.agrovision.data.model.SavePredicitionResponse
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

    // Method untuk menyimpan hasil prediksi
    suspend fun savePrediction(
        imageFile: File,
        userId: String,
        result: String
    ): Response<SavePredicitionResponse> {
        val fileRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val filePart = MultipartBody.Part.createFormData("file", imageFile.name, fileRequestBody)

        val userIdPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
        val resultPart = RequestBody.create("text/plain".toMediaTypeOrNull(), result)

        return agroVisionApi.savePrediction(filePart, userIdPart, resultPart)
    }
}
