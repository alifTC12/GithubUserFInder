package com.aliftc12.githubuserfinder.di

import com.aliftc12.githubuserfinder.data.BaseUrl
import com.aliftc12.githubuserfinder.data.mapper.SearchUserResponseToGithubUsersMapper
import com.aliftc12.githubuserfinder.data.repository.UserRepositoryImpl
import com.aliftc12.githubuserfinder.data.service.UserService
import com.aliftc12.githubuserfinder.domain.repository.UserRepository
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

}