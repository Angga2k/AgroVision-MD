package com.dicoding.agrovision.ui.view.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.agrovision.data.model.PredictionResponse
import com.dicoding.agrovision.data.model.SavePredictionResponse
import com.dicoding.agrovision.data.repository.PredictionRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class PredictionViewModel(private val repository: PredictionRepository) : ViewModel() {

    private val _predictionResult = MutableLiveData<PredictionResponse?>()
    val predictionResult: LiveData<PredictionResponse?> = _predictionResult
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    private val _savePredictionResponse = MutableLiveData<Response<SavePredictionResponse>>()
    val savePredictionResponse: LiveData<Response<SavePredictionResponse>> = _savePredictionResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getPrediction(imageFile: File) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getPrediction(imageFile)
                if (response.isSuccessful) {
                    _predictionResult.postValue(response.body())
                } else {
                    _errorMessage.postValue("Error: ${response.message()}")
                }
            } catch (e: HttpException) {
                _errorMessage.postValue("HTTP Error: ${e.message}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unknown Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun savePrediction(
        authorization: String,
        file: MultipartBody.Part,
        result: RequestBody,
        accuracy: RequestBody
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.savePrediction(authorization, file, result, accuracy)
                if (response.isSuccessful) {
                    _saveResult.postValue(true) // Berhasil
                } else {
                    _saveResult.postValue(false) // Gagal
                    _error.postValue("Error: ${response.message()}") // Tampilkan pesan error
                }
            } catch (e: Exception) {
                _error.postValue(e.message ?: "An unexpected error occurred") // Kesalahan jaringan
            } finally {
                _isLoading.postValue(false) // Selesai
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

}