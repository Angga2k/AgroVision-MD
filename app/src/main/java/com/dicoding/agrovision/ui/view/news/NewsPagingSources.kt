package com.dicoding.agrovision.ui.view.news

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.agrovision.data.model.Article
import com.dicoding.agrovision.data.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class NewsPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getTobaccoNews(
                query = "petani OR pertanian",
                language = "id",
                apiKey = "c1c00ad985ac48d29105fe9a4d52e614",
                page = page,
                pageSize = params.loadSize,
                sortBy = "publishedAt"

            )
            Log.d("NewsResponse", "Response: ${response.body()?.articles}")
            if (response.isSuccessful) {
                Log.d("NewsPagingSource", "Success: ${response.body()?.articles?.size}")
            } else {
                Log.e("NewsPagingSource", "Error: ${response.code()} - ${response.message()}")
            }



            if (response.isSuccessful) {
                val articles = response.body()?.articles.orEmpty()
                LoadResult.Page(
                    data = articles,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (articles.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition
    }
}
