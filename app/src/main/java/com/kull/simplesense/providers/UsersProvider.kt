package com.kull.simplesense.providers

import com.kull.simplesense.api.ApiRoutes
import com.kull.simplesense.models.LoginRequest
import com.kull.simplesense.models.LoginResponse
import com.kull.simplesense.models.Temperature
import com.kull.simplesense.routes.ITemperatureRoutes
import com.kull.simplesense.routes.IUserRoutes
import retrofit2.Call


class UsersProvider {
    private var usersRoutes: IUserRoutes? = null

    init {
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()
    }

    fun login(loginRequest: LoginRequest): Call<LoginResponse>? {
        return usersRoutes?.login(loginRequest)
    }
}