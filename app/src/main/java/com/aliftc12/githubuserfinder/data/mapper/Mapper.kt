package com.aliftc12.githubuserfinder.data.mapper

/*
* Template to create dto to domain mapper
*/
interface Mapper<I, O> {
    fun map(input: I): O
}