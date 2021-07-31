package com.aliftc12.githubuserfinder.data

/*
* Template to create dto to domain mapper
*/
interface Mapper<I, O> {
    fun map(input: I): O
}