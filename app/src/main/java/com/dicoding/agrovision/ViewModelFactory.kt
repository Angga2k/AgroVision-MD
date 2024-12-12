package com.dicoding.agrovision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.agrovision.data.repository.NewsRepository
import com.dicoding.agrovision.data.repository.PredictionRepository
import com.dicoding.agrovision.ui.view.history.HistoryViewModel
import com.dicoding.agrovision.ui.view.news.NewsViewModel
import com.dicoding.agrovision.ui.view.result.PredictionViewModel

class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                NewsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

class PredictionViewModelFactory(private val repository: PredictionRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PredictionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                PredictionViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
class HistoryViewModelFactory(private val repository: PredictionRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                HistoryViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
