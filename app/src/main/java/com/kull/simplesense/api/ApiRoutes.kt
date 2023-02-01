package com.kull.simplesense.api

import com.kull.simplesense.routes.*

class ApiRoutes {
    val API_URL = "http://168.232.167.85/quality-check-server/api/"
    val retrofit = RetrofitClient()

    fun getTemperatureRoutes(): ITemperatureRoutes {
        return retrofit.getClient(API_URL).create(ITemperatureRoutes::class.java)
    }

    fun getPreparationRoutes(): IPreparationRoutes {
        return retrofit.getClient(API_URL).create(IPreparationRoutes::class.java)
    }

    fun getCalibrationRoutes(): ICalibrationRoutes {
        return retrofit.getClient(API_URL).create(ICalibrationRoutes::class.java)
    }

    fun getUsersRoutes(): IUserRoutes {
        return retrofit.getClient(API_URL).create(IUserRoutes::class.java)
    }

    fun getClientsRoutes(): IClientRoutes {
        return retrofit.getClient(API_URL).create(IClientRoutes::class.java)
    }
}