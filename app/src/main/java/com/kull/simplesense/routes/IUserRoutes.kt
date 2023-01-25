package com.kull.simplesense.routes

import com.kull.simplesense.models.LoginRequest
import com.kull.simplesense.models.LoginResponse
import com.kull.simplesense.models.Temperature
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IUserRoutes {
    @POST("auth/sign-in")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
