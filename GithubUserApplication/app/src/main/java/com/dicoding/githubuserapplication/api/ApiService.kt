package com.dicoding.githubuserapplication.api

import com.dicoding.githubuserapplication.dataclasses.DetailUserResponse
import com.dicoding.githubuserapplication.dataclasses.ItemsItem
import com.dicoding.githubuserapplication.dataclasses.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("search/users")
    fun getUser(
        @Query("q") login: String
    ): Call<UserResponse>

    @GET("users/{username}")
    fun getUserDetail(
        @Path("username") username: String
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollowers(
        @Path("username") username : String
    ) : Call<List<ItemsItem>>

    @GET("users/{username}/following")
    fun getFollowing(
        @Path("username") username: String
    ) : Call<List<ItemsItem>>
}