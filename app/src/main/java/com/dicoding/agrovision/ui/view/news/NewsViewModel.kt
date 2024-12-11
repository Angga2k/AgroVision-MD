package com.dicoding.agrovision.ui.view.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.agrovision.data.model.Article
import com.dicoding.agrovision.data.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    val newsPagingData: Flow<PagingData<Article>> =
        repository.getNewsPagingData().cachedIn(viewModelScope)
}
