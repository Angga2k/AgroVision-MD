package com.dicoding.agrovision.ui.view.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.AgroVision.databinding.FragmentNewsBinding
import com.dicoding.agrovision.NewsViewModelFactory
import com.dicoding.agrovision.data.repository.NewsRepository
import com.dicoding.agrovision.data.retrofit.ApiClient
import androidx.paging.PagingDataAdapter

class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsRepository: NewsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)

        // Initialize repository and ViewModel
        newsRepository = NewsRepository(ApiClient.apiService)
        val factory = NewsViewModelFactory(newsRepository)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        // Setup RecyclerView and Adapter
        newsAdapter = NewsAdapter()
        binding.rvNews.layoutManager = LinearLayoutManager(context)
        binding.rvNews.adapter = newsAdapter

        // Observe PagingData from ViewModel
        viewModel.newsPagingData.observe(viewLifecycleOwner, { pagingData ->
            newsAdapter.submitData(lifecycle, pagingData)  // Provide PagingData to the Adapter
        })

        // Add LoadStateListener for loading and error states
        newsAdapter.addLoadStateListener { loadState ->
            // Hide loading indicator when data is loaded
            binding.progressBar.visibility = if (loadState.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            // Show error message if loading failed
            if (loadState.refresh is LoadState.Error) {
                val errorMessage = (loadState.refresh as LoadState.Error).error.localizedMessage
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
