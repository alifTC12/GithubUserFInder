package com.aliftc12.githubuserfinder.di

import com.aliftc12.githubuserfinder.data.BaseUrl
import com.aliftc12.githubuserfinder.data.mapper.SearchUserResponseToGithubUsersMapper
import com.aliftc12.githubuserfinder.data.repository.UserRepositoryImpl
import com.aliftc12.githubuserfinder.data.service.UserService
import com.aliftc12.githubuserfinder.domain.LoadMoreStateAdapter
import com.aliftc12.githubuserfinder.domain.repository.UserRepository
import com.aliftc12.githubuserfinder.presentation.UserFinderViewModel
import com.aliftc12.githubuserfinder.presentation.UserListAdapter
import com.bumptech.glide.Glide
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val userFinderModule = module {

    single<UserService> {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl.GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UserService::class.java)
    }

    single<UserRepository> { UserRepositoryImpl(get(), SearchUserResponseToGithubUsersMapper()) }

    single { Glide.with(androidContext()) }

    viewModel { UserFinderViewModel(get()) }

    factory { UserListAdapter(get()) }

    factory { LoadMoreStateAdapter() }

}