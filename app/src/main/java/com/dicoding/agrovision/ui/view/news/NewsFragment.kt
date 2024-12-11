package com.dicoding.agrovision.ui.view.news

import NewsAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.AgroVision.databinding.FragmentNewsBinding
import com.dicoding.agrovision.NewsViewModelFactory
import com.dicoding.agrovision.data.repository.NewsRepository
import com.dicoding.agrovision.data.retrofit.ApiClient.apiService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private val newsRepository = NewsRepository(apiService)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)

        val factory = NewsViewModelFactory(newsRepository)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        newsAdapter = NewsAdapter { newsUrl ->
            openUrlInBrowser(newsUrl)
        }

        binding.rvNews.layoutManager = LinearLayoutManager(context)
        binding.rvNews.adapter = newsAdapter

        observeNewsData()

        return binding.root
    }

    private fun observeNewsData() {
        // Observe paging data
        lifecycleScope.launch {
            viewModel.newsPagingData.collectLatest { pagingData ->
                newsAdapter.submitData(pagingData)
            }
        }

        // Observe load states for showing/hiding the progress bar
        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collectLatest { loadStates ->
                // Show ProgressBar when data is loading
                binding.progressBar.visibility = if (loadStates.refresh is androidx.paging.LoadState.Loading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                // Handle errors
                if (loadStates.refresh is androidx.paging.LoadState.Error) {
                    Toast.makeText(context, "Failed to load news data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
