package com.aliftc12.githubuserfinder.data.service

import com.aliftc12.githubuserfinder.data.dto.SearchUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
    @GET("search/users")
    suspend fun searchUser(
        @Query("page") page: Int,
        @Query("query") query: String,
    ): Response<SearchUserResponse>
}