package com.dicoding.agrovision.ui.view.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.agrovision.data.model.HistoryResponse
import com.dicoding.agrovision.data.repository.PredictionRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: PredictionRepository) : ViewModel() {

    private val _predictionHistory = MutableLiveData<List<HistoryResponse>>()
    val predictionHistory: LiveData<List<HistoryResponse>> get() = _predictionHistory
    private val _newScan = MutableLiveData<Unit>()
    val newScan: LiveData<Unit> get() = _newScan

    fun getPredictionHistory(token: String) {
        viewModelScope.launch {
            try {
                val history = repository.getPredictionHistory(token)
                _predictionHistory.postValue(history)
            } catch (e: Exception) {
                // Menangani error, misalnya menampilkan pesan kesalahan
                _predictionHistory.postValue(emptyList())
                Log.e("HistoryViewModel", "Error fetching history: ${e.message}")
            }
        }
    }
    fun triggerNewScan() {
        _newScan.value = Unit
    }

}
