package com.kull.simplesense.routes

import com.kull.simplesense.models.Preparation
import retrofit2.Call
import retrofit2.http.GET

interface IPreparationRoutes {
    @GET("preparations")
    fun getPreparations(): Call<ArrayList<Preparation>>
}