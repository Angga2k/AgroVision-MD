package com.dicoding.agrovision.ui.view.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.agrovision.data.model.Article
import com.dicoding.agrovision.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {


    private val _newsLiveData = MutableLiveData<List<Article>>()
    val newsLiveData: LiveData<List<Article>> = _newsLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun fetchNews(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.fetchTobaccoNews(apiKey)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    _newsLiveData.postValue(articles)
                } else {
                    _errorLiveData.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Error: ${e.message}")
            }
        }
    }

}
