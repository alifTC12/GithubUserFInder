package com.aliftc12.githubuserfinder.data.dto

import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @SerializedName("items")
    val users: List<User>,
    @SerializedName("total_count")
    val totalCount: Int
)

data class User(
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val login: String
)