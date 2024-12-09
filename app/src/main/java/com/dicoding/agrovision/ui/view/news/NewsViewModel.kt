package com.dicoding.agrovision.ui.view.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.agrovision.data.model.Article
import com.dicoding.agrovision.data.repository.NewsRepository
import com.dicoding.agrovision.data.paging.NewsPagingSource

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    val newsPagingData: LiveData<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            prefetchDistance = 2
        ),
        pagingSourceFactory = { NewsPagingSource(repository.apiService) }
    ).flow.cachedIn(viewModelScope).asLiveData()
}
