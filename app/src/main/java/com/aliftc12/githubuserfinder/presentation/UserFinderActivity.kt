package com.aliftc12.githubuserfinder.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliftc12.githubuserfinder.databinding.ActivityUserFinderBinding
import com.aliftc12.githubuserfinder.domain.LoadMoreStateAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val VISIBLE_THRESHOLD = 2

class UserFinderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserFinderBinding
    private val viewModel: UserFinderViewModel by viewModel()
    private val userListAdapter: UserListAdapter by inject()
    private val loadMoreStateAdapter: LoadMoreStateAdapter by inject()

    private val endlessLinearScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.userList.layoutManager as LinearLayoutManager
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalCurrentItem = layoutManager.itemCount

            if (lastVisibleItemPosition + VISIBLE_THRESHOLD > totalCurrentItem) {
                viewModel.loadMoreUser(binding.inputEt.text.toString(), totalCurrentItem)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpObserver()
        setupUi()
    }

    private fun setUpObserver() {
        viewModel.searchUserState.observe(this) { state ->
            Log.d("searchUserState", state.toString())
            when (state) {
                SearchUserState.Failed -> {
                }
                SearchUserState.Loading -> {
                }
                SearchUserState.Succeed -> {
                    binding.userList.scrollToPosition(0)
                }
                SearchUserState.HaveNoResult -> {
                }

                SearchUserState.LoadMoreState.AllDataLoaded,
                SearchUserState.LoadMoreState.Failed,
                SearchUserState.LoadMoreState.Loading,
                SearchUserState.LoadMoreState.Succeed -> {
                    loadMoreStateAdapter.submitState(state as SearchUserState.LoadMoreState)
                }
            }
        }

        viewModel.githubUsers.observe(this) { githubUsers ->
            userListAdapter.submitList(githubUsers.toList())
        }
    }

    private fun setupUi() = with(binding) {
        userList.apply {
            addOnScrollListener(endlessLinearScrollListener)
            adapter = ConcatAdapter(userListAdapter, loadMoreStateAdapter)
        }
        searchBtn.setOnClickListener {
            viewModel.searchUser(binding.inputEt.text.toString())
        }
    }
}