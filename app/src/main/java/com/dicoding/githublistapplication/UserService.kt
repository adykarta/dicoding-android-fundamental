package com.dicoding.githublistapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("search/users?")
    fun getSearchUserData(@Query("q") username: String): Call<UserResponse>
    @GET("users/{username}")
    fun getDetailUserData(@Path(value = "username", encoded = true) username: String): Call<UserDetail>
    @GET("users/{username}/followers")
    fun getFollowers(@Path(value = "username", encoded = true) username: String): Call<ArrayList<UserDetail>>
    @GET("users/{username}/following")
    fun getFollowing(@Path(value = "username", encoded = true) username: String): Call<ArrayList<UserDetail>>

}