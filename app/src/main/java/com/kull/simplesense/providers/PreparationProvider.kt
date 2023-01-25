package com.kull.simplesense.providers

import com.kull.simplesense.api.ApiRoutes
import com.kull.simplesense.models.Preparation
import com.kull.simplesense.models.Temperature
import com.kull.simplesense.routes.IPreparationRoutes
import retrofit2.Call

class PreparationProvider {
    private var preparationRoutes: IPreparationRoutes? = null

    init {
        val api = ApiRoutes()
        preparationRoutes = api.getPreparationRoutes()
    }

    fun getPreparations(): Call<ArrayList<Preparation>>? {
        return preparationRoutes?.getPreparations()
    }
}