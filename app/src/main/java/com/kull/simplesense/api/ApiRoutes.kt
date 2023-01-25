package com.kull.simplesense.api

import com.kull.simplesense.routes.*

class ApiRoutes {
    val API_URL = "https://simple-sense-api.herokuapp.com/api/"
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