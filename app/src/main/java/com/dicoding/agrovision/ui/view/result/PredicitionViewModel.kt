package com.dicoding.agrovision.ui.view.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.agrovision.data.model.PredictionResponse
import com.dicoding.agrovision.data.repository.PredictionRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class PredictionViewModel(private val repository: PredictionRepository) : ViewModel() {

    private val _predictionResult = MutableLiveData<PredictionResponse?>()
    val predictionResult: LiveData<PredictionResponse?> = _predictionResult

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
}
