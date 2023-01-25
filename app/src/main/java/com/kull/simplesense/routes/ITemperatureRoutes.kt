package com.kull.simplesense.routes

import com.kull.simplesense.models.Temperature
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ITemperatureRoutes {
    @POST("temperatures")
    fun register(@Body temperature: Temperature): Call<Temperature>
}