package com.example.myprojectwazzz.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("api/users")
    suspend fun getListUsers(
        @Query("page") page: Int = 1
    ): Response<UserResponse>

    @POST("api/users")
    suspend fun createNewUser(
        @Body user: UserItem
    ): Response<UserItem>
}
