package com.aliftc12.githubuserfinder.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliftc12.githubuserfinder.R
import com.aliftc12.githubuserfinder.databinding.ActivityUserFinderBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val VISIBLE_THRESHOLD = 2

class UserFinderActivity : AppCompatActivity(),
    LoadMoreStateAdapter.LoadMoreStateAdapterInteraction {
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
                viewModel.loadMoreUser(amountCurrentUser = totalCurrentItem)
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


    override fun retryLoadMore() {
        viewModel.retryLoadMoreUser()
    }

    private fun setUpObserver() {
        viewModel.searchUserState.observe(this) { state ->
            Log.d("searchUserState", state.toString())
            when (state) {
                SearchUserState.Loading -> {
                    binding.inputLayout.error = null
                    binding.progressBar.visibility = VISIBLE
                    binding.userList.visibility = GONE
                    binding.retryTv.visibility = GONE
                }
                SearchUserState.Failed -> {
                    binding.progressBar.visibility = GONE
                    binding.userList.visibility = GONE
                    binding.retryTv.visibility = VISIBLE
                }
                SearchUserState.Succeed -> {
                    binding.progressBar.visibility = GONE
                    binding.userList.visibility = VISIBLE
                    binding.retryTv.visibility = GONE

                    binding.userList.scrollToPosition(0)
                }
                SearchUserState.HaveNoResult -> {
                    binding.progressBar.visibility = GONE
                    binding.userList.visibility = GONE
                    binding.retryTv.visibility = GONE

                    binding.inputLayout.error = getString(R.string.msg_search_no_result)
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
        loadMoreStateAdapter.setListener(this@UserFinderActivity)
        retryTv.setOnClickListener { viewModel.searchUser() }
        searchBtn.setOnClickListener { viewModel.searchUser(binding.inputEt.text.toString()) }
        binding.inputEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) binding.inputLayout.error = null
            }

        })
    }
}