package com.example.buddy.data.network

import com.example.buddy.data.user.User
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {

    @GET("api/users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): User
}