package com.dicoding.agrovision.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.agrovision.data.model.Article
import com.dicoding.agrovision.data.retrofit.ApiService
import com.dicoding.agrovision.ui.view.news.NewsPagingSource
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val apiService: ApiService) {

    fun getNewsPagingData(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = { NewsPagingSource(apiService) }
        ).flow
    }
}
