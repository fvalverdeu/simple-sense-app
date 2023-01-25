package com.kull.simplesense.providers

import com.kull.simplesense.api.ApiRoutes
import com.kull.simplesense.models.Temperature
import com.kull.simplesense.routes.ITemperatureRoutes
import retrofit2.Call

class TemperatureProvider {
    private var temperatureRoutes: ITemperatureRoutes? = null

    init {
        val api = ApiRoutes()
        temperatureRoutes = api.getTemperatureRoutes()
    }

    fun register(temperature: Temperature): Call<Temperature>? {
        return temperatureRoutes?.register(temperature)
    }
}