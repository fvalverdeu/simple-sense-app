package com.kull.simplesense.providers

import com.kull.simplesense.api.ApiRoutes
import com.kull.simplesense.models.Calibration
import com.kull.simplesense.routes.ICalibrationRoutes
import retrofit2.Call

class CalibrationProvider {
    private var calibrationRoutes: ICalibrationRoutes? = null

    init {
        val api = ApiRoutes()
        calibrationRoutes = api.getCalibrationRoutes()
    }

    fun register(calibration: Calibration): Call<Calibration>? {
        return calibrationRoutes?.register(calibration)
    }
}