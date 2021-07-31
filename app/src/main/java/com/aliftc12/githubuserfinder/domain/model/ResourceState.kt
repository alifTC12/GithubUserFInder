package com.aliftc12.githubuserfinder.domain.model

sealed class ResourceState<T> {
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error<T>(val throwable: Throwable? = null, val responseCode: Int? = null) :
        ResourceState<T>()
}
