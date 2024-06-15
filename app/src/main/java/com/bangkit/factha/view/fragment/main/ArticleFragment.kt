package com.bangkit.factha.view.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.data.network.ApiConfig
import com.bangkit.factha.data.preference.UserPreferences
import com.bangkit.factha.data.preference.dataStore
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.databinding.FragmentArticleBinding
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainViewModel
import com.bangkit.factha.view.adapter.HomeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var repository: MainRepository
    private var userId: String? = null

    // Obtain viewModel instance using viewModels delegate
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
        val token = runBlocking { userPreferences.token.first() }
        if (token != null) {
            val apiService = ApiConfig.getMainService(token)
            repository = MainRepository.getInstance(apiService, userPreferences)

            observeNews()
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)

        CoroutineScope(Dispatchers.IO).launch {
            userId = userPreferences.userId.first()
            userId?.let {
                withContext(Dispatchers.Main) {
                    binding.rvArticle.layoutManager = LinearLayoutManager(requireContext())
                    homeAdapter = HomeAdapter(emptyList(), it, repository)
                    binding.rvArticle.adapter = homeAdapter
                }
            }
        }
    }

    private fun observeNews() {
        viewModel.news.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingMenuArticle.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.loadingMenuArticle.visibility = View.GONE
                    val newsData = result.data.newsData ?: emptyList()
                    userId?.let {
                        homeAdapter = HomeAdapter(newsData, it, repository)
                        binding.rvArticle.adapter = homeAdapter
                    }
                }
                is Result.Error -> {
                    Log.e("TAG", "Observe news failed")
                }
            }
        }
        viewModel.getNews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
