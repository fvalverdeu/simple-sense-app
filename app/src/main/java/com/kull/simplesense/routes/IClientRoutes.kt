package com.kull.simplesense.routes

import com.kull.simplesense.models.Client
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IClientRoutes {
    @GET("clients/by-user/{iduser}")
    fun getClientsByUser(@Path("iduser") iduser: String): Call<ArrayList<Client>>
}
